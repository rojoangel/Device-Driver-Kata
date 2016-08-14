package codekata;

import codekata.exception.WriteError;

/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {

    public static final byte PROGRAM_COMMAND = (byte) 0x40;

    private FlashMemoryDevice hardware;
    private HardwareReader hardwareReader;
    private HardwareWriter hardwareWriter;
    private WriteOperationVerifier writeVerifier;

    public DeviceDriver(FlashMemoryDevice hardware, Timer timer) {
        this.hardware = hardware;
        this.hardwareReader = new HardwareReader(hardware);
        this.hardwareWriter = new HardwareWriter(hardware);
        this.writeVerifier = new WriteOperationVerifier(hardware, timer);
    }

    public byte read(long address) {
        return hardwareReader.read(address);
    }

    public void write(long address, byte data) throws Exception {
        writeProgramCommand();
        writeToHardware(address, data);
        verifyWrite(address, data);
    }

    private void verifyWrite(long address, byte data) throws WriteError {
        this.writeVerifier.verify(address, data);
    }

    private void writeToHardware(long address, byte data) {
        hardwareWriter.write(address, data);
    }

    private void writeProgramCommand() {
        hardwareWriter.write(0x0, PROGRAM_COMMAND);
    }

}
