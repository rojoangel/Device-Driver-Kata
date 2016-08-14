package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.WriteError;
import codekata.exception.write.VoltageError;
import codekata.writer.WriteErrorHandler;

public class VoltageErrorHandler implements WriteErrorHandler {

    private FlashMemoryDevice hardware;

    public VoltageErrorHandler(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    @Override
    public void handle() throws WriteError {
        setHardwareReadyToAcceptNewWrites();
        throw new VoltageError();
    }

    private void setHardwareReadyToAcceptNewWrites() {
        hardware.write((long) 0x0, (byte) 0xFF);
    }
}
