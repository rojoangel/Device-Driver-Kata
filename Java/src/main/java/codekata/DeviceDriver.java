package codekata;

import codekata.exception.WriteError;
import codekata.reader.HardwareReader;
import codekata.writer.HardwareWriter;
import codekata.writer.WriteOperationVerifier;

/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {

    public static final byte PROGRAM_COMMAND = (byte) 0x40;

    private HardwareReader hardwareReader;
    private HardwareWriter hardwareWriter;

    public DeviceDriver(FlashMemoryDevice hardware, Timer timer) {
        WriteOperationVerifier writeVerifier = new WriteOperationVerifier(hardware, timer);
        this.hardwareReader = new HardwareReader(hardware);
        this.hardwareWriter = new HardwareWriter(hardware, writeVerifier);
    }

    public byte read(long address) {
        return hardwareReader.read(address);
    }

    public void write(long address, byte data) throws Exception {
        writeProgramCommand();
        writeToHardware(address, data);
    }

    private void writeToHardware(long address, byte data) throws WriteError {
        hardwareWriter.write(address, data);
    }

    private void writeProgramCommand() {
        hardwareWriter.writeToHardware(0x0, PROGRAM_COMMAND);
    }

}
