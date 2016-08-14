package codekata.writer;

import codekata.FlashMemoryDevice;
import codekata.Timer;
import codekata.exception.WriteError;

public class HardwareWriter {

    public static final byte PROGRAM_COMMAND = (byte) 0x40;

    private final FlashMemoryDevice hardware;
    private WriteOperationVerifier verifier;

    public HardwareWriter(FlashMemoryDevice hardware, Timer timer) {
        this.hardware = hardware;
        this.verifier = new WriteOperationVerifier(hardware, timer);
    }

    public void write(long address, byte data) throws WriteError {
        writeProgramCommand();
        writeToHardware(address, data);
        verifyWrite(address, data);
    }

    private void verifyWrite(long address, byte data) throws WriteError {
        verifier.verify(address, data);
    }

    private void writeToHardware(long address, byte data) {
        hardware.write(address, data);
    }

    private void writeProgramCommand() {
        this.writeToHardware(0x0, PROGRAM_COMMAND);
    }
}
