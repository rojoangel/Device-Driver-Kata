package codekata.writer.handler;

import codekata.FlashMemoryDevice;
import codekata.exception.write.VoltageError;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VoltageErrorHandlerTest {

    private Mockery context;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();
    }

    @Test(expected = VoltageError.class)
    public void handle() throws Exception {
        FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        context.checking(new Expectations() {{
            oneOf(hardware).write((long) 0x0, (byte) 0xFF);
        }});

        VoltageErrorHandler handler = new VoltageErrorHandler(hardware);
        handler.handle();
    }

    @After
    public void tearDown() throws Exception {
        context.assertIsSatisfied();
    }
}
