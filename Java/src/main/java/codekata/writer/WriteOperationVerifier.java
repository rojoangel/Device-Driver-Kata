package codekata.writer;

import codekata.FlashMemoryDevice;
import codekata.Timer;
import codekata.exception.WriteError;
import codekata.exception.write.*;

public class WriteOperationVerifier {

    private FlashMemoryDevice hardware;
    private Timer timer;

    public WriteOperationVerifier(FlashMemoryDevice hardware, Timer timer) {
        this.hardware = hardware;
        this.timer = timer;
    }

    public void verify(long address, byte data) throws WriteError {
        byte readByte = waitForWriteOperationToComplete();
        switch (readByte) {
            case 0b0001000000:
                if (!verifyWrittenData(address, data)) {
                    throw new WriteVerificationError();
                }
                break;
            case 0b0001000100:
                setHardwareReadyToAcceptNewWrites();
                throw new VoltageError();
            case 0b0001001000:
                setHardwareReadyToAcceptNewWrites();
                throw new InternalHardwareError();
            case 0b0001010000:
                setHardwareReadyToAcceptNewWrites();
                throw new ProtectedBlockError();
        }
    }

    private byte waitForWriteOperationToComplete() throws ReadyBitTimeoutError {
        do {
            byte readByte = read(0x0);
            if (isReadyBitSet(readByte)) {
                return readByte;
            }
            if (timeout()) {
                throw new ReadyBitTimeoutError();
            }
        } while (true);
    }

    private boolean isReadyBitSet(byte readByte) {
        return (readByte & 0b0001000000) == 0b0001000000;
    }

    private boolean verifyWrittenData(long address, byte data) {
        return read(address) == data;
    }

    private void setHardwareReadyToAcceptNewWrites() {
        write(0x0, (byte) 0xFF);
    }

    private boolean timeout() {
        return timer.hasTimedOut();
    }

    private byte read(long address) {
        return hardware.read(address);
    }

    private void write(long address, byte data) {
        hardware.write(address, data);
    }
}
