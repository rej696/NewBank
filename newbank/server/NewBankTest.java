package newbank.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewBankTest {

    private String username = "tester";
    private String password = "tester";

    @Test
    void showAccounts() {
        // Inizialisation
        NewBank teste = NewBank.getBank();
        CustomerID customerID = teste.checkLogInDetails(username, password);

        // When the account details are requested
        String result = teste.processRequest(customerID, "SHOWMYACCOUNTS");

        // Should the response show an account name und the current balance
        Assertions.assertEquals("Main: 1000.0", result);
    }
}