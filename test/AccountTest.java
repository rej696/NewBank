package test;

import newbank.server.Customer;
import newbank.server.CustomerID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTest extends test.MainTest {

    @Test
    public void showAccounts() {
        String result = test.processRequest(this.customerId1, "SHOWMYACCOUNTS");
        Assertions.assertEquals("12345678 Main: 1000.0\n", result);
    }

    @Test
    public void createAccounts() {
        String result = test.processRequest(this.customerId1, "NEWACCOUNT SAVING");
        Assertions.assertEquals("Account with name SAVING was created", result);
    }


    @Test
    public void showAccountsNoAccount() {
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWMYACCOUNTS");
        Assertions.assertEquals("ERROR: Customer " + clientId.getKey() + " has no accounts", result);
    }

    @Test
    public void createAccountWithToManyCharacters() {
         String result = test.processRequest(this.customerId1, "NEWACCOUNT 1234567890123456789012345678901");
        Assertions.assertEquals("Account not created. Illegal account name", result);
    }

    @Test
    public void createAccountWithIllegalCharacters() {
        String result = test.processRequest(this.customerId1, "NEWACCOUNT SAVING!");
        Assertions.assertEquals("Account not created. Illegal account name", result);
    }
}
