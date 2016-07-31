package codekata;

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
        if (wasWriteOperationSuccessful(waitForWriteOperationToComplete())) {
            if (!verifyWriteOperation(address, data)) {
                throw new WriteVerificationError();
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
