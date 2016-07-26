package codekata;

/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {

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
        // TODO: implement this method
    }
}
