package codekata.writer;

import codekata.FlashMemoryDevice;
import codekata.Timer;
import codekata.exception.WriteError;
import codekata.exception.write.*;
import codekata.writer.handler.InternalHardwareErrorHandler;
import codekata.writer.handler.ProtectedBlockErrorHandler;
import codekata.writer.handler.VoltageErrorHandler;

import java.util.HashMap;
import java.util.Map;

public class WriteOperationVerifier {

    private final Map<Byte, WriteErrorHandler> errorHandlers = new HashMap<>();
    private final WriteOperationCompletionChecker writeOperationCompletenessChecker;
    private FlashMemoryDevice hardware;

    public WriteOperationVerifier(FlashMemoryDevice hardware, Timer timer) {
        this.hardware = hardware;
        writeOperationCompletenessChecker = new WriteOperationCompletionChecker(hardware, timer);
        registerErrorHandlers();
    }

    private void registerErrorHandlers() {
        registerErrorHandler(new VoltageErrorHandler(this.hardware));
        registerErrorHandler(new InternalHardwareErrorHandler(this.hardware));
        registerErrorHandler(new ProtectedBlockErrorHandler(this.hardware));
    }

    private void registerErrorHandler(WriteErrorHandler handler) {
        this.errorHandlers.put(handler.handles(), handler);
    }

    public void verify(long address, byte data) throws WriteError {
        byte readByte = waitForWriteOperationToComplete();
        if (isSuccessfulWrite(readByte)) {
            handleWriteSuccess(address, data);
            return;
        }
        handleWriteError(readByte);
    }

    private boolean isSuccessfulWrite(byte readByte) {
        return readByte == 0b0001000000;
    }

    private void handleWriteSuccess(long address, byte data) throws WriteVerificationError {
        if (!verifyWrittenData(address, data)) {
            throw new WriteVerificationError();
        }
    }

    private void handleWriteError(byte readByte) throws WriteError {
        if (errorHandlers.containsKey(readByte)) {
            errorHandlers.get(readByte).handle();
        }
    }

    private byte waitForWriteOperationToComplete() throws ReadyBitTimeoutError {
        return writeOperationCompletenessChecker.check();
    }

    private boolean verifyWrittenData(long address, byte data) {
        return read(address) == data;
    }

    private byte read(long address) {
        return hardware.read(address);
    }
}
