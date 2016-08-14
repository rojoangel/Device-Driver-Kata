package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.WriteError;
import codekata.exception.write.InternalHardwareError;
import codekata.writer.HardwareResetter;
import codekata.writer.WriteErrorHandler;

public class InternalHardwareErrorHandler implements WriteErrorHandler {

    private final HardwareResetter hardwareResetter;

    public InternalHardwareErrorHandler(FlashMemoryDevice hardware) {
        this.hardwareResetter = new HardwareResetter(hardware);
    }

    @Override
    public byte handles() {
        return 0b0001001000;
    }

    @Override
    public void handle() throws WriteError {
        hardwareResetter.reset();
        throw new InternalHardwareError();
    }
}
