package codekata;

import codekata.exception.WriteError;
import codekata.reader.HardwareReader;
import codekata.writer.HardwareWriter;

/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {

    private HardwareReader hardwareReader;
    private HardwareWriter hardwareWriter;

    public DeviceDriver(FlashMemoryDevice hardware, Timer timer) {
        this.hardwareReader = new HardwareReader(hardware);
        this.hardwareWriter = new HardwareWriter(hardware, timer);
    }

    public byte read(long address) {
        return hardwareReader.read(address);
    }

    public void write(long address, byte data) throws WriteError {
        hardwareWriter.write(address, data);
    }
}
