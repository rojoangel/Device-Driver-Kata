package codekata;

import codekata.exception.WriteError;
import codekata.exception.write.*;

/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {

    public static final byte PROGRAM_COMMAND = (byte) 0x40;

    private FlashMemoryDevice hardware;
    private Timer timer;
    private WriteOperationVerifier writeVerifier;

    private FlashMemoryDevice getHardware() {
        return hardware;
    }

    public DeviceDriver(FlashMemoryDevice hardware, Timer timer) {
        this.hardware = hardware;
        this.timer = timer;
        this.writeVerifier = new WriteOperationVerifier();
    }

    public byte read(long address) {
        return getHardware().read(address);
    }

    public void write(long address, byte data) throws Exception {
        writeProgramCommand();
        writeToHardware(address, data);
        verifyWrite(address, data);
    }

    private void verifyWrite(long address, byte data) throws WriteError {
        this.writeVerifier.verify(address, data);
    }

    private byte readFromHardware(long address) {
        return getHardware().read(address);
    }

    private void writeToHardware(long address, byte data) {
        getHardware().write(address, data);
    }

    private void writeProgramCommand() {
        writeToHardware(0x0, PROGRAM_COMMAND);
    }

    private class WriteOperationVerifier {

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

        private byte waitForWriteOperationToComplete() throws ReadyBitTimeoutError{
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
    }
}
