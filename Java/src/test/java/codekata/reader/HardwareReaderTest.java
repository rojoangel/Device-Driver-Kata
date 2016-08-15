package codekata.reader;

import codekata.FlashMemoryDevice;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HardwareReaderTest {


    private Mockery context;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();
    }

    @Test
    public void testRead() throws Exception {
        int memoryAddress = 0xAB;
        byte memoryValue = 0b00010001;

        FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        context.checking(new Expectations() {{
            oneOf(hardware).read(memoryAddress);
            will(returnValue(memoryValue));
        }});

        HardwareReader reader = new HardwareReader(hardware);
        assertEquals(memoryValue, reader.read(memoryAddress));
    }

    @After
    public void tearDown() throws Exception {
        context.assertIsSatisfied();
    }
}