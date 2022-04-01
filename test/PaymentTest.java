package test;

import newbank.server.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PaymentTest extends test.MainTest {

    @Test
    public void moveFunds() {
        this.customer1.addAccount(new Account("23456789", "Savings", 1001));

        String result = test.processRequest(this.customerId1, "MOVE 100 12345678 23456789");
        Assertions.assertEquals("Success. 100.0 moved from 12345678 to 23456789\n\nNew Balance\n\n12345678 Main: 900.0\n23456789 Savings: 1101.0", result);
    }

    @Test
    public void makePayment() {
        String result = test.processRequest(this.customerId1, "PAY 100 12345678 23456789");
        Assertions.assertEquals("Success. 100.0 paid from 12345678 to 23456789\n\nNew Balance\n\n12345678 Main: 900.0", result);
    }
}
