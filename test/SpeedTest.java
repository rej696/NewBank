package test;

import org.junit.jupiter.api.Test;

public class SpeedTest extends test.MainTest {

    @Test
    public void speedTest() {
        for (int i=0; i<500; i++) {
            test.processRequest(this.customerId1, "OFFERLOAN 1 12345678 365 5");
        }
    }
}
