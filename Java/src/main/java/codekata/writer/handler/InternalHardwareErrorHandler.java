package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.WriteError;
import codekata.exception.write.InternalHardwareError;
import codekata.writer.WriteErrorHandler;

public class InternalHardwareErrorHandler implements WriteErrorHandler {

    private FlashMemoryDevice hardware;

    public InternalHardwareErrorHandler(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    public void handle() throws WriteError {
        setHardwareReadyToAcceptNewWrites();
        throw new InternalHardwareError();
    }
    private void setHardwareReadyToAcceptNewWrites() {
        hardware.write((long) 0x0, (byte) 0xFF);
    }
}
