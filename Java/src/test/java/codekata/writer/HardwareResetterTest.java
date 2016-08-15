package codekata.writer;

import codekata.FlashMemoryDevice;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HardwareResetterTest {

    private Mockery context;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();
    }

    @Test
    public void testReset() throws Exception {
        FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        context.checking(new Expectations() {{
            oneOf(hardware).write((long) 0x0, (byte) 0xFF);
        }});

        HardwareResetter resetter = new HardwareResetter(hardware);
        resetter.reset();
    }

    @After
    public void tearDown() throws Exception {
        context.assertIsSatisfied();
    }
}
