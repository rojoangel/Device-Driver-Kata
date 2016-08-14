package codekata.writer;

import codekata.FlashMemoryDevice;
import codekata.exception.WriteError;

public class HardwareWriter {
    private final FlashMemoryDevice hardware;
    private WriteOperationVerifier verifier;

    public HardwareWriter(FlashMemoryDevice hardware, WriteOperationVerifier verifier) {
        this.hardware = hardware;
        this.verifier = verifier;
    }

    public void write(long address, byte data) throws WriteError {
        hardware.write(address, data);
        verifier.verify(address, data);
    }

    public void writeToHardware(long address, byte data) {
        hardware.write(address, data);
    }
}