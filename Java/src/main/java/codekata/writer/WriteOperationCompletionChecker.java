package codekata.writer;

import codekata.FlashMemoryDevice;
import codekata.Timer;
import codekata.exception.write.ReadyBitTimeoutError;

public class WriteOperationCompletionChecker {

    private FlashMemoryDevice hardware;
    private Timer timer;

    public WriteOperationCompletionChecker(FlashMemoryDevice hardware, Timer timer) {
        this.hardware = hardware;
        this.timer = timer;
    }

    public byte check() throws ReadyBitTimeoutError {
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

    private boolean timeout() {
        return timer.hasTimedOut();
    }

    private byte read(long address) {
        return hardware.read(address);
    }
}