package codekata;

import codekata.exception.write.*;
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
        int address = 0xAB;
        byte memoryValue = 0b00010001;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        final Timer timer = context.mock(Timer.class);

        context.checking(new Expectations() {{
            ignoring(timer);
            oneOf(hardware).read(address);
            will(returnValue(memoryValue));
        }});

        DeviceDriver driver = new DeviceDriver(hardware, timer);
        byte readValue = driver.read(address);
        context.assertIsSatisfied();

        assertEquals(memoryValue, readValue);
    }

    @Test
    public void succcessful_write_To_Hardware() throws Exception {

        final int address = 0xFF;
        final byte value = 100;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        final Timer timer = context.mock(Timer.class);

        context.checking(new Expectations() {{
            ignoring(timer);
//          Begin by writing the 'Program' command, 0x40 to address 0x0
            oneOf(hardware).write(0x0, (byte) 0x40);
//        Then make a call to write the data to the address you want to write to.
            oneOf(hardware).write(address, value);
//        Then read the value in address 0x0 and check bit 7 in the returned data, also known as the 'ready bit'.
//          Repeat the read operation until the ready bit is set to 1. This means the write operation is complete.
//          In a typical device it should take around ten microseconds, but it will vary from write to write.
            exactly(4).of(hardware).read(0x0);
            will(onConsecutiveCalls(
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0001000000)));
//          If all of them are set to 0 then the operation was successful.
//        You should then make a read from the memory address you previously set,
//          in order to check it returns the value you set.
//        If that is successful, then you can assume the whole write operation was successful.
            oneOf(hardware).read(address);
            will(returnValue(value));
        }});

        DeviceDriver driver = new DeviceDriver(hardware, timer);
        driver.write(address, value);

        context.assertIsSatisfied();
    }

    @Test(expected = WriteVerificationError.class)
    public void write_Verification_Error_On_Write_To_Hardware() throws Exception {

        final int address = 0xFF;
        final byte value = 100;
        final byte readVerificationFailureValue = 0b0000000001;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        final Timer timer = context.mock(Timer.class);

        context.checking(new Expectations() {{
            ignoring(timer);
//          Begin by writing the 'Program' command, 0x40 to address 0x0
            oneOf(hardware).write(0x0, (byte) 0x40);
//        Then make a call to write the data to the address you want to write to.
            oneOf(hardware).write(address, value);
//        Then read the value in address 0x0 and check bit 7 in the returned data, also known as the 'ready bit'.
//          Repeat the read operation until the ready bit is set to 1. This means the write operation is complete.
//          In a typical device it should take around ten microseconds, but it will vary from write to write.
            exactly(4).of(hardware).read(0x0);
            will(onConsecutiveCalls(
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0001000000)));
//          If all of them are set to 0 then the operation was successful.
//        You should then make a read from the memory address you previously set,
//          in order to check it returns the value you set.
//        If that is successful, then you can assume the whole write operation was successful.
            oneOf(hardware).read(address);
            will(returnValue(readVerificationFailureValue));
        }});

        DeviceDriver driver = new DeviceDriver(hardware, timer);
        driver.write(address, value);

        context.assertIsSatisfied();
    }

    @Test(expected = VoltageError.class)
    public void voltage_Error_On_Write_To_Hardware() throws Exception {

        final int address = 0xFF;
        final byte value = 100;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        final Timer timer = context.mock(Timer.class);

        context.checking(new Expectations() {{
            ignoring(timer);
//          Begin by writing the 'Program' command, 0x40 to address 0x0
            oneOf(hardware).write(0x0, (byte) 0x40);
//        Then make a call to write the data to the address you want to write to.
            oneOf(hardware).write(address, value);
//        Then read the value in address 0x0 and check bit 7 in the returned data, also known as the 'ready bit'.
//          Repeat the read operation until the ready bit is set to 1. This means the write operation is complete.
//          In a typical device it should take around ten microseconds, but it will vary from write to write.
            exactly(4).of(hardware).read(0x0);
            will(onConsecutiveCalls(
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
//        In the case of an error, the device sets the one of the other bits in the data at location 0x0
//          bit 3: Vpp error. The voltage on the device was wrong and it is now physically damaged.
                    returnValue((byte) 0b0001000100)));
//        If any of these error bits are set, you must write 0xFF to address 0x0
//          before the device will accept any new write requests.
//          This will reset the error bits to zero. Note that until the 'ready bit' is set,
//          then you will not get valid values for any of the error bits.
            oneOf(hardware).write(0x0, (byte) 0xFF);
        }});

        DeviceDriver driver = new DeviceDriver(hardware, timer);
        driver.write(address, value);

        context.assertIsSatisfied();
    }

    @Test(expected = InternalHardwareError.class)
    public void internal_Error_On_Write_To_Hardware() throws Exception {

        final int address = 0xFF;
        final byte value = 100;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        final Timer timer = context.mock(Timer.class);

        context.checking(new Expectations() {{
            ignoring(timer);
//          Begin by writing the 'Program' command, 0x40 to address 0x0
            oneOf(hardware).write(0x0, (byte) 0x40);
//        Then make a call to write the data to the address you want to write to.
            oneOf(hardware).write(address, value);
//        Then read the value in address 0x0 and check bit 7 in the returned data, also known as the 'ready bit'.
//          Repeat the read operation until the ready bit is set to 1. This means the write operation is complete.
//          In a typical device it should take around ten microseconds, but it will vary from write to write.
            exactly(4).of(hardware).read(0x0);
            will(onConsecutiveCalls(
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
//        In the case of an error, the device sets the one of the other bits in the data at location 0x0
//          bit 4: internal error. Something went wrong this time but next time it might work.
                    returnValue((byte) 0b0001001000)));
//        If any of these error bits are set, you must write 0xFF to address 0x0
//          before the device will accept any new write requests.
//          This will reset the error bits to zero. Note that until the 'ready bit' is set,
//          then you will not get valid values for any of the error bits.
            oneOf(hardware).write(0x0, (byte) 0xFF);
        }});

        DeviceDriver driver = new DeviceDriver(hardware, timer);
        driver.write(address, value);

        context.assertIsSatisfied();
    }

    @Test(expected = ProtectedBlockError.class)
    public void protected_Block_Error_On_Write_To_Hardware() throws Exception {

        final int address = 0xFF;
        final byte value = 100;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        final Timer timer = context.mock(Timer.class);
        context.checking(new Expectations() {{
            ignoring(timer);
//          Begin by writing the 'Program' command, 0x40 to address 0x0
            oneOf(hardware).write(0x0, (byte) 0x40);
//        Then make a call to write the data to the address you want to write to.
            oneOf(hardware).write(address, value);
//        Then read the value in address 0x0 and check bit 7 in the returned data, also known as the 'ready bit'.
//          Repeat the read operation until the ready bit is set to 1. This means the write operation is complete.
//          In a typical device it should take around ten microseconds, but it will vary from write to write.
            exactly(4).of(hardware).read(0x0);
            will(onConsecutiveCalls(
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
                    returnValue((byte) 0b0000000000),
//        In the case of an error, the device sets the one of the other bits in the data at location 0x0
//          bit 4: internal error. Something went wrong this time but next time it might work.
                    returnValue((byte) 0b0001010000)));
//        If any of these error bits are set, you must write 0xFF to address 0x0
//          before the device will accept any new write requests.
//          This will reset the error bits to zero. Note that until the 'ready bit' is set,
//          then you will not get valid values for any of the error bits.
            oneOf(hardware).write(0x0, (byte) 0xFF);
        }});

        DeviceDriver driver = new DeviceDriver(hardware, timer);
        driver.write(address, value);

        context.assertIsSatisfied();
    }
    @Test(expected = ReadyBitTimeoutError.class)
    public void ready_Bit_TimeOut_Error_On_Write_To_Hardware() throws Exception {

        final int address = 0xFF;
        final byte value = 100;

        final FlashMemoryDevice hardware = context.mock(FlashMemoryDevice.class);
        final Timer timer = context.mock(Timer.class);
        context.checking(new Expectations() {{
//          Begin by writing the 'Program' command, 0x40 to address 0x0
            oneOf(hardware).write(0x0, (byte) 0x40);
//        Then make a call to write the data to the address you want to write to.
            oneOf(hardware).write(address, value);
//        Then read the value in address 0x0 and check bit 7 in the returned data, also known as the 'ready bit'.
//          Repeat the read operation until the ready bit is set to 1. This means the write operation is complete.
//          In a typical device it should take around ten microseconds, but it will vary from write to write.
            oneOf(hardware).read(0x0);
            will(returnValue((byte) 0b0000000000));

            oneOf(timer).hasTimedOut();
            will(returnValue(true));

        }});

        DeviceDriver driver = new DeviceDriver(hardware, timer);
        driver.write(address, value);

        context.assertIsSatisfied();
    }
}

