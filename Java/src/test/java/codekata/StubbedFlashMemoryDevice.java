package codekata;

class StubbedFlashMemoryDevice implements FlashMemoryDevice {

    private byte memoryValue;

    StubbedFlashMemoryDevice(byte memoryValue) {
        this.memoryValue = memoryValue;
    }

    public byte read(long address) {
        return memoryValue;
    }

    public void write(long address, byte data) {

    }
}
