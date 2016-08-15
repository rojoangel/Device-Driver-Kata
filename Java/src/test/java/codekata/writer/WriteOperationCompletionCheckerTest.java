package codekata.writer;

import codekata.FlashMemoryDevice;
import codekata.Timer;
import codekata.exception.write.ReadyBitTimeoutError;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class WriteOperationCompletionCheckerTest {

    private Mockery context;

    @Parameter
    public byte readValue;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();
    }

    @Test(expected = ReadyBitTimeoutError.class)
    public void testTimeout() throws Exception {
        FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        Timer timer = context.mock(Timer.class);
        context.checking(new Expectations() {{
            ignoring(hardware);
            oneOf(timer).start();
            oneOf(timer).hasTimedOut();
            will(returnValue(true));
        }});

        WriteOperationCompletionChecker verifier = new WriteOperationCompletionChecker(hardware, timer);
        verifier.check();
    }

    @Test
    public void testReadsUntilReadyBitIsSet() throws Exception {
        FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        Timer timer = context.mock(Timer.class);
        context.checking(new Expectations() {{
            ignoring(timer);
            exactly(4).of(hardware).read(0x0);
            will(onConsecutiveCalls(
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0001000000)));
        }});

        WriteOperationCompletionChecker verifier = new WriteOperationCompletionChecker(hardware, timer);
        byte readByte = verifier.check();
        assertEquals(0b0001000000, readByte);
    }

    @Test
    public void testExitsWhenReadyBitIsSet() throws Exception {
        FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        Timer timer = context.mock(Timer.class);
        context.checking(new Expectations() {{
            ignoring(timer);
            oneOf(hardware).read(0x0);
            will(returnValue(readValue));
        }});

        WriteOperationCompletionChecker verifier = new WriteOperationCompletionChecker(hardware, timer);
        byte readByte = verifier.check();
        assertEquals(readValue, readByte);
    }

    @Parameters
    public static Object[] data() {
        return new Object[] {
                (byte) 0b1001000000, (byte) 0b0101000000, (byte) 0b0011000000, (byte) 0b0001100000,
                (byte) 0b0001010000, (byte) 0b0001001000, (byte) 0b0001000100, (byte) 0b0001000010,
                (byte) 0b0001000001, (byte) 0b1111111111, (byte) 0b0101010101
        };
    }

    @After
    public void tearDown() throws Exception {
        context.assertIsSatisfied();
    }
}
