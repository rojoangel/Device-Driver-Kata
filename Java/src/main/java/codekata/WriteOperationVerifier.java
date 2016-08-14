package codekata;

import codekata.exception.WriteError;
import codekata.exception.write.*;

class WriteOperationVerifier {

    private HardwareReader hardwareReader;
    private HardwareWriter hardwareWriter;
    private Timer timer;

    public WriteOperationVerifier(HardwareReader hardwareReader, HardwareWriter hardwareWriter, Timer timer) {
        this.hardwareReader = hardwareReader;
        this.hardwareWriter = hardwareWriter;
        this.timer = timer;
    }

    public void verify(long address, byte data) throws WriteError {
        byte readByte = waitForWriteOperationToComplete();
        if (wasWriteOperationSuccessful(readByte)) {
            if (!verifyWrittenData(address, data)) {
                throw new WriteVerificationError();
            }
        } else {
            setHardwareReadyToAcceptNewWrites();
            switch (readByte) {
                case 0b0001000100:
                    throw new VoltageError();
                case 0b0001001000:
                    throw new InternalHardwareError();
                case 0b0001010000:
                    throw new ProtectedBlockError();
            }
        }
    }

    private byte waitForWriteOperationToComplete() throws ReadyBitTimeoutError {
        byte readByte;
        do {
            readByte = readFromHardware(0x0);
            if (isReadyBitSet(readByte)) {
                break;
            }
            if (timeout()) {
                throw new ReadyBitTimeoutError();
            }
        } while (true);
        return readByte;
    }

    private boolean wasWriteOperationSuccessful(byte readByte) {
        return readByte == 0b0001000000;
    }

    private boolean isReadyBitSet(byte readByte) {
        return (readByte & 0b0001000000) == 0b0001000000;
    }

    private boolean verifyWrittenData(long address, byte data) {
        return readFromHardware(address) == data;
    }

    private void setHardwareReadyToAcceptNewWrites() {
        writeToHardware(0x0, (byte) 0xFF);
    }

    private boolean timeout() {
        return timer.hasTimedOut();
    }

    private byte readFromHardware(long address) {
        return hardwareReader.read(address);
    }

    private void writeToHardware(long address, byte data) {
        hardwareWriter.write(address, data);
    }
}
