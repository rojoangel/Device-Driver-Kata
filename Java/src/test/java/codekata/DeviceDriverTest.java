package codekata;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DeviceDriverTest {

    private Mockery context;

    @Before
    public void setUp() {
        context = new Mockery();
    }

    @Test
    public void read_From_Hardware() {
        byte memoryValue = 0;
        FlashMemoryDevice hardware = new StubbedFlashMemoryDevice(memoryValue);

        DeviceDriver driver = new DeviceDriver(hardware);
        byte data = driver.read(0xFF);

        assertEquals(memoryValue, data);
    }

    @Test
    public void succcessful_write_To_Hardware() throws Exception {

        final int address = 0xFF;
        final byte value = 100;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        context.checking(new Expectations() {{
//          Begin by writing the 'Program' command, 0x40 to address 0x0
            oneOf(hardware).write(0x0, (byte) 0x40);
//        Then make a call to write the data to the address you want to write to.
            oneOf(hardware).write(address, value);
//        Then read the value in address 0x0 and check bit 7 in the returned data, also known as the 'ready bit'.
//          Repeat the read operation until the ready bit is set to 1. This means the write operation is complete.
//          In a typical device it should take around ten microseconds, but it will vary from write to write.
            oneOf(hardware).read(0x0);
            will(returnValue((byte) 0b0001000000));
//        There could have been an error, so in the value from address 0x0, examine the contents of the other bits.
//          If all of them are set to 0 then the operation was successful.
//        You should then make a read from the memory address you previously set,
//          in order to check it returns the value you set.
//        If that is successful, then you can assume the whole write operation was successful.
        }});
        DeviceDriver driver = new DeviceDriver(hardware);
        driver.write(address, value);

        context.assertIsSatisfied();
    }
}

