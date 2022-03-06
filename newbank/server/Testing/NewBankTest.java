package newbank.server.Testing;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewBankTest {

    private String username = "tester";
    private String password = "tester";

    @Test
    void showAccounts() {
        // Inizialisation
        NewBank test = NewBank.getBank();

        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678","Main",1000));
        CustomerID clientId = new CustomerID("TestID1");
        test.addCustomer(testCustomer, clientId.getKey());

        // When the account details are requested
        String result = test.processRequest(clientId, "SHOWMYACCOUNTS");

        // Should the response show an account name und the current balance
        Assertions.assertEquals("12345678 Main: 1000.0\n", result);
    }

    @Test
    void createAccounts() {
        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678","Main",1000));
        CustomerID clientId = new CustomerID("TestID1");
        test.addCustomer(testCustomer, clientId.getKey());

        // When the account details are requested
        String result = test.processRequest(clientId, "NEWACCOUNT SAVING");

        Assertions.assertEquals("Account with name SAVING was created", result);
    }


    @Test
    void showAccountsNoAccount() {
        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID2");
        test.addCustomer(testCustomer, clientId.getKey());

        // When the account details are requested
        String result = test.processRequest(clientId, "SHOWMYACCOUNTS");

        // Should the response show an account name und the current balance
        Assertions.assertEquals("Error customer has no accounts", result);
    }

    @Test
    void createAccountWithToManyCharacters(){

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT 1234567890123456789012345678901");

        Assertions.assertEquals("Account not created. Illegal account name", result);

    }

    @Test
    void createAccountWithIllegalCharacters(){

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT SAVING!");

        Assertions.assertEquals("Account not created. Illegal account name", result);

    }

    @Test
    void moveFunds(){

        // Inizialisation
        NewBank test = NewBank.getBank();
        CustomerID customerID = test.checkLogInDetails(username, password);
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678","Current",1000));
        testCustomer.addAccount(new Account("23456789","Savings",1001));
        CustomerID clientId = new CustomerID("TestID4");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "MOVE 100 12345678 23456789");

        Assertions.assertEquals("Success. 100.0 moved from 12345678 to 23456789\n\nNew Balance\n\n12345678 Current: 900.0\n23456789 Savings: 1101.0", result);

    }

    @Test
    void makePayment(){

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678","Current",1000));
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        testCustomer2.addAccount(new Account("23456789","Current",1000));
        test.addCustomer(testCustomer2, "TestID6");

        String result = test.processRequest(clientId, "PAY 100 12345678 23456789");

        Assertions.assertEquals("Success. 100.0 payed from 12345678 to 23456789\n\nNew Balance\n\n12345678 Current: 900.0", result);

    }
}