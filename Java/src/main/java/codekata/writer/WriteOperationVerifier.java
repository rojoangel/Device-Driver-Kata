package codekata.writer;

import codekata.FlashMemoryDevice;
import codekata.Timer;
import codekata.exception.WriteError;
import codekata.exception.write.*;

import java.util.HashMap;
import java.util.Map;

public class WriteOperationVerifier {

    private final Map<Byte, WriteErrorHandler> errorHandlers = new HashMap<>();
    private FlashMemoryDevice hardware;
    private Timer timer;

    public WriteOperationVerifier(FlashMemoryDevice hardware, Timer timer) {
        this.hardware = hardware;
        this.errorHandlers.put((byte) 0b0001000100, new VoltageErrorHandler(this.hardware));
        this.errorHandlers.put((byte) 0b0001001000, new InternalHardwareErrorHandler(this.hardware));
        this.errorHandlers.put((byte) 0b0001010000, new ProtectedBlockErrorHandler(this.hardware));
        this.timer = timer;
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
        do {
            byte readByte = read(0x0);
            if (isReadyBitSet(readByte)) {
                return readByte;
            }
            if (timeout()) {
                throw new ReadyBitTimeoutError();
            }
        } while (true);
    }

    private boolean isReadyBitSet(byte readByte) {
        return (readByte & 0b0001000000) == 0b0001000000;
    }

    private boolean verifyWrittenData(long address, byte data) {
        return read(address) == data;
    }

    private boolean timeout() {
        return timer.hasTimedOut();
    }

    private byte read(long address) {
        return hardware.read(address);
    }
}
