package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.WriteError;
import codekata.exception.write.VoltageError;
import codekata.writer.HardwareResetter;
import codekata.writer.WriteErrorHandler;

public class VoltageErrorHandler implements WriteErrorHandler {

    private final HardwareResetter hardwareResetter;

    public VoltageErrorHandler(FlashMemoryDevice hardware) {
        this.hardwareResetter = new HardwareResetter(hardware);
    }

    @Override
    public byte handles() {
        return 0b0001000100;
    }

    @Override
    public void handle() throws WriteError {
        hardwareResetter.reset();
        throw new VoltageError();
    }
}
