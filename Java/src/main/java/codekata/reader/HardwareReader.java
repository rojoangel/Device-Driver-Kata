package codekata.reader;

import codekata.FlashMemoryDevice;

public class HardwareReader {
    private final FlashMemoryDevice hardware;

    public HardwareReader(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    public byte read(long address) {
        return hardware.read(address);
    }
}
