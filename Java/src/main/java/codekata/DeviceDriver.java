package codekata;

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

    public void write(long address, byte data) {
        writeProgramCommand();
        hardware.write(address, data);
        byte readByte;
        do {
            readByte = hardware.read(0x0);
        } while ((readByte & 0b0001000000) != 0b0001000000);
    }

    private void writeProgramCommand() {
        hardware.write(0x0, PROGRAM_COMMAND);
    }
}
