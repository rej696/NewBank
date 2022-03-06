package newbank.server.Testing;

import static org.junit.jupiter.api.Assertions.*;

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
        CustomerID customerID = test.checkLogInDetails(username, password);

        String result = test.processRequest(customerID, "NEWACCOUNT SAVING");

        Assertions.assertEquals("Account with name SAVING was created", result);


        // When the account details are requested
        String result2 = test.processRequest(customerID, "SHOWMYACCOUNTS");

        // Should the response show an account name und the current balance
        Assertions.assertEquals("Main: 1000.0\nSAVING: 0.0", result2);
    }

    @Test
    void showAccountsNoAccount() {
        // Inizialisation
        NewBank test = NewBank.getBank();
        CustomerID customerID = test.checkLogInDetails("tester2", "tester2");

        // When the account details are requested
        String result = test.processRequest(customerID, "SHOWMYACCOUNTS");

        // Should the response show an account name und the current balance
        Assertions.assertEquals("Error customer has no accounts", result);
    }

    @Test
    void createAccountWithToManyCharacters(){

        // Inizialisation
        NewBank test = NewBank.getBank();
        CustomerID customerID = test.checkLogInDetails(username, password);

        String result = test.processRequest(customerID, "NEWACCOUNT 1234567890123456789012345678901");

        Assertions.assertEquals("Account not created. Illegal account name", result);

    }

    @Test
    void createAccountWithIllegalCharacters(){

        // Inizialisation
        NewBank test = NewBank.getBank();
        CustomerID customerID = test.checkLogInDetails(username, password);

        String result = test.processRequest(customerID, "NEWACCOUNT SAVING!");

        Assertions.assertEquals("Account not created. Illegal account name", result);

    }
}