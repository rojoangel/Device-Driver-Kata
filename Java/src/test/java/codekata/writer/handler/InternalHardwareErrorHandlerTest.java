package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.write.InternalHardwareError;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InternalHardwareErrorHandlerTest {

    private Mockery context;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();

    }

    @Test(expected = InternalHardwareError.class)
    public void testHandle() throws Exception {
        FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        context.checking(new Expectations() {{
            oneOf(hardware).write((long) 0x0, (byte) 0xFF);
        }});

        InternalHardwareErrorHandler handler = new InternalHardwareErrorHandler(hardware);
        handler.handle();
    }

    @After
    public void tearDown() throws Exception {
        context.assertIsSatisfied();
    }
}