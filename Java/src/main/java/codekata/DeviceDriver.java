package codekata;

import codekata.exception.InternalHardwareError;
import codekata.exception.ProtectedBlockError;
import codekata.exception.VoltageError;
import codekata.exception.WriteVerificationError;

/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {

    public static final byte PROGRAM_COMMAND = (byte) 0x40;

    private FlashMemoryDevice hardware;

    private FlashMemoryDevice getHardware() {
        return hardware;
    }

    public DeviceDriver(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    public byte read(long address) {
        return getHardware().read(address);
    }

    public void write(long address, byte data) throws Exception {
        writeProgramCommand();
        writeToHardware(address, data);
        byte readByte = waitForWriteOperationToComplete();
        if (wasWriteOperationSuccessful(readByte)) {
            if (!verifyWriteOperation(address, data)) {
                throw new WriteVerificationError();
            }
        } else {
            switch (readByte) {
                case 0b0001000100:
                    writeToHardware(0x0, (byte) 0xFF);
                    throw new VoltageError();
                case 0b0001001000:
                    writeToHardware(0x0, (byte) 0xFF);
                    throw new InternalHardwareError();
                case 0b0001010000:
                    writeToHardware(0x0, (byte) 0xFF);
                    throw new ProtectedBlockError();
            }
        }
    }

    private boolean verifyWriteOperation(long address, byte data) {
        return readFromHardware(address) == data;
    }

    private boolean wasWriteOperationSuccessful(byte readByte) {
        return readByte == 0b0001000000;
    }

    private byte readFromHardware(long address) {
        return hardware.read(address);
    }

    private void writeToHardware(long address, byte data) {
        hardware.write(address, data);
    }

    private byte waitForWriteOperationToComplete() {
        byte readByte;
        do {
            readByte = readFromHardware(0x0);
        } while ((readByte & 0b0001000000) != 0b0001000000);
        return readByte;
    }

    private void writeProgramCommand() {
        writeToHardware(0x0, PROGRAM_COMMAND);
    }
}
