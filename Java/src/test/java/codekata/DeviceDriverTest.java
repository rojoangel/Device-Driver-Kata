package codekata;

import static org.junit.Assert.*;

import org.junit.Test;


public class DeviceDriverTest {

    @Test
    public void read_From_Hardware() {
        byte memoryValue = 0;
        FlashMemoryDevice hardware = new StubbedFlashMemoryDevice(memoryValue);

        DeviceDriver driver = new DeviceDriver(hardware);
        byte data = driver.read(0xFF);

        assertEquals(memoryValue, data);
    }

}

