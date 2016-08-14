package codekata.writer;

import codekata.FlashMemoryDevice;

public class HardwareWriter {
    private final FlashMemoryDevice hardware;

    public HardwareWriter(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    public void write(long address, byte data) {
        hardware.write(address, data);
    }
}