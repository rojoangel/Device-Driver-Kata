package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.WriteError;
import codekata.exception.write.ProtectedBlockError;
import codekata.writer.WriteErrorHandler;

public class ProtectedBlockErrorHandler implements WriteErrorHandler {
    private FlashMemoryDevice hardware;

    public ProtectedBlockErrorHandler(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    public void handle() throws WriteError {
        setHardwareReadyToAcceptNewWrites();
        throw new ProtectedBlockError();
    }
    private void setHardwareReadyToAcceptNewWrites() {
        write(0x0, (byte) 0xFF);
    }

    private void write(long address, byte data) {
        hardware.write(address, data);
    }

}