package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.WriteError;
import codekata.exception.write.ProtectedBlockError;
import codekata.writer.HardwareResetter;
import codekata.writer.WriteErrorHandler;

public class ProtectedBlockErrorHandler implements WriteErrorHandler {
    private final HardwareResetter hardwareResetter;

    public ProtectedBlockErrorHandler(FlashMemoryDevice hardware) {
        this.hardwareResetter = new HardwareResetter(hardware);
    }

    public void handle() throws WriteError {
        hardwareResetter.reset();
        throw new ProtectedBlockError();
    }
}
