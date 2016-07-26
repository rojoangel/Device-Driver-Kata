package codekata;

class StubbedFlashMemoryDevice implements FlashMemoryDevice {

    public byte read(long address) {
        return 0;
    }

    public void write(long address, byte data) {

    }
}
