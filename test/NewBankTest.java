import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NewBankTest {

    @Test
    public void showAccounts() {
        // Inizialisation
        NewBank test = NewBank.getBank();

        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Main", 1000));
        CustomerID clientId = new CustomerID("TestID1");
        test.addCustomer(testCustomer, clientId.getKey());

        // When the account details are requested
        String result = test.processRequest(clientId, "SHOWMYACCOUNTS");

        // Should the response show an account name und the current balance
        assertTrue(result.equals("12345678 Main: 1000.0\n"));
    }

    @Test
    public void createAccounts() {
        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Main", 1000));
        CustomerID clientId = new CustomerID("TestID1");
        test.addCustomer(testCustomer, clientId.getKey());

        // When the account details are requested
        String result = test.processRequest(clientId, "NEWACCOUNT SAVING");

        assertTrue(result.equals("Account with name SAVING was created"));
    }


    @Test
    public void showAccountsNoAccount() {
        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID2");
        test.addCustomer(testCustomer, clientId.getKey());

        // When the account details are requested
        String result = test.processRequest(clientId, "SHOWMYACCOUNTS");

        assertTrue(result.equals("ERROR: Customer " + clientId.getKey() + " has no accounts"));
    }

    @Test
    public void createAccountWithToManyCharacters() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT 1234567890123456789012345678901");

        assertTrue(result.equals("Account not created. Illegal account name"));
    }

    @Test
    public void createAccountWithIllegalCharacters() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT SAVING!");

        assertTrue(result.equals("Account not created. Illegal account name"));
    }

    @Test
    public void moveFunds() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Current", 1000));
        testCustomer.addAccount(new Account("23456789", "Savings", 1001));
        CustomerID clientId = new CustomerID("TestID4");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "MOVE 100 12345678 23456789");

        assertTrue(result.equals("Success. 100.0 moved from 12345678 to 23456789\n\nNew Balance\n\n12345678 Current: 900.0\n23456789 Savings: 1101.0"));
    }

    @Test
    public void makePayment() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        testCustomer2.addAccount(new Account("23456789", "Current", 1000));
        test.addCustomer(testCustomer2, "TestID6");

        String result = test.processRequest(clientId, "PAY 100 12345678 23456789");

        assertTrue(result.equals("Success. 100.0 paid from 12345678 to 23456789\n\nNew Balance\n\n12345678 Current: 900.0"));
    }
}