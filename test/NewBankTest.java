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
        CustomerID clientId = new CustomerID("TestID26");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        test.clearLoans();
    }

    @Test
    public void offerLoanInsufficientFunds() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("22345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID24");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 1001 22345678 365 5");

        Assertions.assertEquals("ERROR. Loan of 1001.0 cannot be offered from 22345678. Loan amount exceeds funds.", result);
        test.clearLoans();
    }

    @Test
    public void offerLoanInterestNotInRange() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("32345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID23");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 32345678 365 11");

        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 32345678 with interest 11%. Interest is too high.", result);
        test.clearLoans();
    }

    @Test
    public void offerLoanNotExistingAccountNumber() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("42345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID22");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 00000000 365 5");

        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 00000000. Account is non-existent.", result);
        test.clearLoans();
    }

    @Test
    public void offerLoanFreezeLoanAmount() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("52345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID21");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 52345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 52345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());
        test.clearLoans();
    }

    @Test
    public void showMyOfferLoans() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("62345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID20");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 62345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 62345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId, "OFFERLOAN 500 62345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 62345678 for 365 days with interest of 5%", result2);
        Assertions.assertEquals(0, account.getAvailableBalance());

        String result3 = test.processRequest(clientId, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("Loan Number: 1, Account Number: 62345678, Amount: 500.0, Interest Rate: 5%\nLoan Number: 2, Account Number: 62345678, Amount: 500.0, Interest Rate: 5%\n", result3);
        test.clearLoans();
    }

    @Test
    public void showMyOfferedLoansNoLoansOffered() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("72345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID11");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("No loans offered", result);
        test.clearLoans();
    }

    @Test
    public void ShowOpenLoans() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("82345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID6");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("23456789", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID7");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 82345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 82345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId, "OFFERLOAN 500 82345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 82345678 for 365 days with interest of 5%", result2);
        Assertions.assertEquals(0, account.getAvailableBalance());

        String result3 = test.processRequest(clientId2, "SHOWOPENLOANS");

        Assertions.assertEquals("Loan Number: 1, Amount: 500.0, Term: 365 days, Interest Rate: 5%\nLoan Number: 2, Amount: 500.0, Term: 365 days, Interest Rate: 5%\n", result3);
        test.clearLoans();
    }

    @Test
    public void ShowOpenLoansNoLoansAvailable() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("92345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID9");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("23456789", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID10");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId2, "SHOWOPENLOANS");

        Assertions.assertEquals("No loans available at present", result);
        test.clearLoans();
    }

    @Test
    public void acceptLoan() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("02345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID30");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("11111111", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID31");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 02345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 02345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 11111111");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 11111111.", result2);
        Assertions.assertEquals(1500, account2.getAvailableBalance());
        test.clearLoans();
    }

    @Test
    public void acceptLoanInvalidLoanNumber() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("00000000", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID32");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("22222222", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID33");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 00000000 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 00000000 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 2 22222222");

        Assertions.assertEquals("Error. Invalid loan number.", result2);
        test.clearLoans();
    }

    @Test
    public void acceptLoanInvalidAccountNumber() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("33333333", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID34");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("44444444", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID35");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 33333333 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 33333333 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 00");

        Assertions.assertEquals("Error. Invalid account number.", result2);
        test.clearLoans();
    }

    @Test
    public void showMyOfferedAndAcceptedLoans() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        Account account = new Account("55555555", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID40");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("55555557", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID41");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 55555555 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 55555555 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 55555557");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 55555557.", result2);

        String result3 = test.processRequest(clientId, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("Loan Number: 1, Account Number: 55555555, Amount: 500.0, Interest Rate: 5%, Taken by: 55555557\n", result3);
        test.clearLoans();
    }

    @Test
    public void getHelp() {

        // Inizialisation
        NewBank test = NewBank.getBank();
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("13579111", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID45");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "HELP");

        assertTrue(result.equals("\nPossible commands\n" +
                "Commands must be followed by user input values between <> and separated by a space\n\n" +
                "SHOWMYACCOUNTS\t\t\tShows all of the current customer's account details\n" +
                "NEWACCOUNT <New account name>\t\tCreates a new account for the current customer with the specified name\n" +
                "MOVE <Amount> <Debit account> <Credit account>\tMoves the amount specified between two of a customer's accounts\n" +
                "PAY <Amount> <Debit account> <Credit account>\tPays funds from one account to another account, which may be held by another customer\n" +
                "HELP\t\t\tShows this menu\n\n"));
    }

}
