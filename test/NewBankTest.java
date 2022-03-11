package newbank.server.Testing;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals("12345678 Main: 1000.0\n", result);
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

        Assertions.assertEquals("Account with name SAVING was created", result);
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

        Assertions.assertEquals("ERROR: Customer " + clientId.getKey() + " has no accounts", result);
    }

    @Test
    public void createAccountWithToManyCharacters() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT 1234567890123456789012345678901");

        Assertions.assertEquals("Account not created. Illegal account name", result);
    }

    @Test
    public void createAccountWithIllegalCharacters() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT SAVING!");

        Assertions.assertEquals("Account not created. Illegal account name", result);
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

        Assertions.assertEquals("Success. 100.0 moved from 12345678 to 23456789\n\nNew Balance\n\n12345678 Current: 900.0\n23456789 Savings: 1101.0", result);
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

        Assertions.assertEquals("Success. 100.0 paid from 12345678 to 23456789\n\nNew Balance\n\n12345678 Current: 900.0", result);
    }

    @Test
    public void offerLoan() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
    }

    @Test
    public void offerLoanInsufficientFunds() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 1001 12345678 365 5");

        Assertions.assertEquals("ERROR. Loan of 1001.0 cannot be offered from 12345678. Loan amount exceeds funds.", result);
    }

    @Test
    public void offerLoanInterestNotInRange() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 12345678 365 11");

        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 12345678 with interest 11%. Interest is too high.", result);
    }

    @Test
    public void offerLoanNotExistingAccountNumber() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 00000000 365 5");

        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 00000000. Account is non-existent.", result);
    }

    @Test
    public void offerLoanFreezeLoanAmount() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("12345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());
    }

    @Test
    public void showMyOfferLoans() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("12345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result2);
        Assertions.assertEquals(0, account.getAvailableBalance());

        String result3 = test.processRequest(clientId, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("Loan Number: 1, Account Number: 12345678, Amount: 500.0, Interest Rate: 5%\nLoan Number: 2, Account Number: 12345678, Amount: 500.0, Interest Rate: 5%\n", result3);
    }

    @Test
    public void showMyOfferedLoansNoLoansOffered() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("12345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID5");
        test.addCustomer(testCustomer, clientId.getKey());

        String result3 = test.processRequest(clientId, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("No loans offered", result3);
    }
}
