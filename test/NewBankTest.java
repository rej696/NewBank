import org.junit.Test;
import static org.junit.Assert.assertTrue;
import newbank.server.CustomerID;
import newbank.server.NewBank;

public class NewBankTest {

    private String username = "tester";
    private String password = "tester";

    @Test
    public void showAccounts() {
        // Inizialisation
        NewBank teste = NewBank.getBank();
        CustomerID customerID = teste.checkLogInDetails(username, password);

        // When the account details are requested
        String result = teste.processRequest(customerID, "SHOWMYACCOUNTS");

        // Should the response show an account name und the current balance
        assertTrue(result.equals("Main: 1000.0"));
    }
}
