package codekata.writer;

import codekata.FlashMemoryDevice;

public class HardwareResetter {

    private FlashMemoryDevice hardware;

    public HardwareResetter(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    public void reset() {
        hardware.write((long) 0x0, (byte) 0xFF);
    }
}
