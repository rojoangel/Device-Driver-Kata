package codekata;

import static org.junit.Assert.*;

import org.junit.Test;


public class DeviceDriverTest {

    @Test
    public void read_From_Hardware() {
        FlashMemoryDevice hardware = new StubbedFlashMemoryDevice();
        DeviceDriver driver = new DeviceDriver(hardware);
        byte data = driver.read(0xFF);
        assertEquals(0, data);
    }

}

