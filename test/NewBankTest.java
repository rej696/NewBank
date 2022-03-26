package test;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.Loan;
import newbank.server.NewBank;
import java.time.LocalDate;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewBankTest {
    private NewBank test = NewBank.getBank();
    private StubLocalDate currentTime;

    public void setCurrentTime(LocalDate date) {
        currentTime.set(date);
        Loan.currentTime = (Supplier<LocalDate>) currentTime;
    }
    
    @BeforeEach
    public void setUp() {
        currentTime = new StubLocalDate(LocalDate.of(1996, 3, 6));
        Loan.currentTime = (Supplier<LocalDate>) currentTime;
    }

    @AfterEach
    public void tearDown() {
        test.clearLoans();
    }

    @Test
    public void showAccounts() {
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
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID2");
        test.addCustomer(testCustomer, clientId.getKey());

        // When the account details are requested
        String result = test.processRequest(clientId, "SHOWMYACCOUNTS");

        Assertions.assertEquals("ERROR: Customer " + clientId.getKey() + " has no accounts", result);
    }

    @Test
    public void createAccountWithToManyCharacters() {
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT 1234567890123456789012345678901");

        Assertions.assertEquals("Account not created. Illegal account name", result);
    }

    @Test
    public void createAccountWithIllegalCharacters() {
        Customer testCustomer = new Customer();
        CustomerID clientId = new CustomerID("TestID3");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT SAVING!");

        Assertions.assertEquals("Account not created. Illegal account name", result);
    }

    @Test
    public void moveFunds() {
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
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("12345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID26");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
    }

    @Test
    public void offerLoanInsufficientFunds() {
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("22345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID24");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 1001 22345678 365 5");

        Assertions.assertEquals("ERROR. Loan of 1001.0 cannot be offered from 22345678. Loan amount exceeds funds.", result);
    }

    @Test
    public void offerLoanInterestNotInRange() {
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("32345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID23");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 32345678 365 11");

        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 32345678 with interest 11%. Interest is too high.", result);
    }

    @Test
    public void offerLoanNotExistingAccountNumber() {
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("42345678", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID22");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 00000000 365 5");

        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 00000000. Account is non-existent.", result);
    }

    @Test
    public void offerLoanFreezeLoanAmount() {
        Customer testCustomer = new Customer();
        Account account = new Account("52345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID21");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 52345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 52345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());
    }

    @Test
    public void showMyOfferLoans() {
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
    }

    @Test
    public void showMyOfferedLoansNoLoansOffered() {
        Customer testCustomer = new Customer();
        Account account = new Account("72345678", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID11");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("No loans offered", result);
    }

    @Test
    public void ShowOpenLoans() {
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
    }

    @Test
    public void ShowOpenLoansNoLoansAvailable() {
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
    }

    @Test
    public void acceptLoan() {
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
    }

    @Test
    public void acceptLoanInvalidLoanNumber() {
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
    }

    @Test
    public void acceptLoanInvalidAccountNumber() {
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
    }

    @Test
    public void showMyOfferedAndAcceptedLoans() {
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
    }

    @Test
    public void getHelp() {
        Customer testCustomer = new Customer();
        testCustomer.addAccount(new Account("13579111", "Current", 1000));
        CustomerID clientId = new CustomerID("TestID45");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "HELP");

        Assertions.assertEquals("\nPossible commands\n" +
                "Commands must be followed by user input values between <> and separated by a space\n\n" +
                "SHOWMYACCOUNTS\t\t\t\t\t\t\t\t\t\tShows all of the current customer's account details\n" +
                "NEWACCOUNT <New account name>\t\t\t\t\t\tCreates a new account for the current customer with the specified name\n" +
                "MOVE <Amount> <Debit account> <Credit account>\t\tMoves the amount specified between two of a customer's accounts\n" +
                "PAY <Amount> <Debit account> <Credit account>\t\tPays funds from one account to another account, which may be held by another customer\n" +
                "OFFERLOAN <Amount> <FromAccount> <Terms> <intrest>\tCreates a loan for the specified period, under the defined conditions\n" +
                "SHOWMYOFFEREDLOANS\t\t\t\t\t\t\t\t\tShows all offered loans of the current customer\n" +
                "SHOWOPENLOANS\t\t\t\t\t\t\t\t\t\tShows all open loans with the conditions of the loan.\n" +
                "ACCEPTLOAN <Loan Number> <Account>\t\t\t\t\tThe open loan is accepted and the amount is credited to the given account.\n" +
                "PAYBACKLOAN <Loan Number>\t\t\t\t\t\t\tThe loan is repaid with interest\n" +
                "SHOWTAKENLOANS\t\t\t\t\t\t\t\t\t\tShows all taken loans of the current customer\n" +
                "HELP\t\t\t\t\t\t\t\t\t\t\t\tShows this menu\n\n", result);
    }
  
    @Test
    public void paybackLoan() {
        Customer testCustomer = new Customer();
        Account account = new Account("55555888", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID48");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("55555999", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID51");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 55555888 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 55555888 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 55555999");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 55555999.", result2);

        setCurrentTime(LocalDate.of(1996, 3, 14));

        String result3 = test.processRequest(clientId, "PAYBACKLOAN 1");

        Assertions.assertEquals("Success. Loan Number: 1, Account Number From: 55555999, Account Number To: 55555888, Amount: 500.55\n", result3);

        Assertions.assertTrue(account.getAvailableBalance() > 1500.00);
        Assertions.assertTrue(account2.getAvailableBalance() < 1000.00);
    }

    @Test
    public void invalidLoanNumberPayback() {
        Customer testCustomer = new Customer();
        Account account = new Account("55555222", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID73");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("55555111", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID94");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 55555222 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 55555222 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 55555111");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 55555111.", result2);

        String result3 = test.processRequest(clientId, "PAYBACKLOAN 2");

        Assertions.assertEquals("Error. Invalid loan number.", result3);
    }

    @Test
    public void insufficientFunds() {
        Customer testCustomer = new Customer();
        Account account = new Account("44444222", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID98");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("77777111", "Current", 25);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID99");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 44444222 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 44444222 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 77777111");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 77777111.", result2);
        account2.debit(26);
        String result3 = test.processRequest(clientId, "PAYBACKLOAN 1");

        Assertions.assertEquals("Error. Insufficient funds.", result3);
    }
  
    @Test
    public void showTakenLoans() {
        Customer testCustomer = new Customer();
        Account account = new Account("66666666", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID100");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("77777777", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID101");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 66666666 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 66666666 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 77777777");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 77777777.", result2);

        String result3 = test.processRequest(clientId2, "SHOWTAKENLOANS");

        Assertions.assertEquals("Loan Number: 1, Account Number: 66666666, Amount: 500.0, Interest Rate: 5%, Taken by: 77777777\n", result3);
    }
      
    @Test
    public void showTakenLoansNoLoansTaken() {
        Customer testCustomer = new Customer();
        Account account = new Account("88888888", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID102");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWTAKENLOANS");

        Assertions.assertEquals("No loans taken", result);
    }

    @Test
    public void partPayBackLoan() {
        Customer testCustomer = new Customer();
        Account account = new Account("66666666", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID100");
        test.addCustomer(testCustomer, clientId.getKey());

        Customer testCustomer2 = new Customer();
        Account account2 = new Account("77777777", "Current", 1000);
        testCustomer2.addAccount(account2);
        CustomerID clientId2 = new CustomerID("TestID101");
        test.addCustomer(testCustomer2, clientId2.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN 500 66666666 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 66666666 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, account.getAvailableBalance());

        String result2 = test.processRequest(clientId2, "ACCEPTLOAN 1 77777777");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 77777777.", result2);

        String result3 = test.processRequest(clientId2, "PARTPAYBACKLOAN 1 50");

        Assertions.assertEquals("Success. Loan Number: 1, Account Number From: 77777777, Account Number To: 66666666, Amount: 50.00\n", result3);

        String result4 = test.processRequest(clientId2, "SHOWOPENLOANS");

        Assertions.assertEquals("Loan Number: 1, Amount: 450.0, Term: 365 days, Interest Rate: 5%\n", result4);
    }

    @Test
    public void getHelpShowMyAccounts() {
        Customer testCustomer = new Customer();
        Account account = new Account("12121212", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID150");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWMYACCOUNTS --help");
        String result2 = test.processRequest(clientId, "SHOWMYACCOUNTS -h");

        Assertions.assertEquals("SHOWMYACCOUNTS\t\t\t\t\t\t\t\t\t\tShows all of the current customer's account details\n", result);
        Assertions.assertEquals("SHOWMYACCOUNTS\t\t\t\t\t\t\t\t\t\tShows all of the current customer's account details\n", result2);
    }

    @Test
    public void getHelpNewAccount() {
        Customer testCustomer = new Customer();
        Account account = new Account("13131313", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID151");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "NEWACCOUNT --help");
        String result2 = test.processRequest(clientId, "NEWACCOUNT -h");

        Assertions.assertEquals("NEWACCOUNT <New account name>\t\t\t\t\t\tCreates a new account for the current customer with the specified name\n", result);
        Assertions.assertEquals("NEWACCOUNT <New account name>\t\t\t\t\t\tCreates a new account for the current customer with the specified name\n", result2);
    }

    @Test
    public void getHelpMove() {
        Customer testCustomer = new Customer();
        Account account = new Account("15151515", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "MOVE --help");
        String result2 = test.processRequest(clientId, "MOVE -h");

        Assertions.assertEquals("MOVE <Amount> <Debit account> <Credit account>\t\tMoves the amount specified between two of a customer's accounts\n", result);
        Assertions.assertEquals("MOVE <Amount> <Debit account> <Credit account>\t\tMoves the amount specified between two of a customer's accounts\n", result2);
    }

    @Test
    public void getHelpPay() {
        Customer testCustomer = new Customer();
        Account account = new Account("16161616", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "PAY --help");
        String result2 = test.processRequest(clientId, "PAY -h");

        Assertions.assertEquals("PAY <Amount> <Debit account> <Credit account>\t\tPays funds from one account to another account, which may be held by another customer\n", result);
        Assertions.assertEquals("PAY <Amount> <Debit account> <Credit account>\t\tPays funds from one account to another account, which may be held by another customer\n", result2);
    }

    @Test
    public void getHelpOfferLoan() {
        Customer testCustomer = new Customer();
        Account account = new Account("17171717", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "OFFERLOAN --help");
        String result2 = test.processRequest(clientId, "OFFERLOAN -h");

        Assertions.assertEquals("OFFERLOAN <Amount> <FromAccount> <Terms> <intrest>\tCreates a loan for the specified period, under the defined conditions\n", result);
        Assertions.assertEquals("OFFERLOAN <Amount> <FromAccount> <Terms> <intrest>\tCreates a loan for the specified period, under the defined conditions\n", result2);
    }

    @Test
    public void getHelpShowMyOfferedLoans() {
        Customer testCustomer = new Customer();
        Account account = new Account("18181818", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWMYOFFEREDLOANS --help");
        String result2 = test.processRequest(clientId, "SHOWMYOFFEREDLOANS -h");

        Assertions.assertEquals("SHOWMYOFFEREDLOANS\t\t\t\t\t\t\t\t\tShows all offered loans of the current customer\n", result);
        Assertions.assertEquals("SHOWMYOFFEREDLOANS\t\t\t\t\t\t\t\t\tShows all offered loans of the current customer\n", result2);
    }

    @Test
    public void getHelpShowOpenLoans() {
        Customer testCustomer = new Customer();
        Account account = new Account("19191919", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWOPENLOANS --help");
        String result2 = test.processRequest(clientId, "SHOWOPENLOANS -h");

        Assertions.assertEquals("SHOWOPENLOANS\t\t\t\t\t\t\t\t\t\tShows all open loans with the conditions of the loan.\n", result);
        Assertions.assertEquals("SHOWOPENLOANS\t\t\t\t\t\t\t\t\t\tShows all open loans with the conditions of the loan.\n", result2);
    }

    @Test
    public void getHelpAcceptLoan() {
        Customer testCustomer = new Customer();
        Account account = new Account("20202020", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "ACCEPTLOAN --help");
        String result2 = test.processRequest(clientId, "ACCEPTLOAN -h");

        Assertions.assertEquals("ACCEPTLOAN <Loan Number> <Account>\t\t\t\t\tThe open loan is accepted and the amount is credited to the given account.\n", result);
        Assertions.assertEquals("ACCEPTLOAN <Loan Number> <Account>\t\t\t\t\tThe open loan is accepted and the amount is credited to the given account.\n", result2);
    }

    @Test
    public void getHelpPayBackLoan() {
        Customer testCustomer = new Customer();
        Account account = new Account("21212121", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "PAYBACKLOAN --help");
        String result2 = test.processRequest(clientId, "PAYBACKLOAN -h");

        Assertions.assertEquals("PAYBACKLOAN <Loan Number>\t\t\t\t\t\t\tThe loan is repaid with interest\n", result);
        Assertions.assertEquals("PAYBACKLOAN <Loan Number>\t\t\t\t\t\t\tThe loan is repaid with interest\n", result2);
    }

    @Test
    public void getHelpShowTakenLoans() {
        Customer testCustomer = new Customer();
        Account account = new Account("23232323", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        String result = test.processRequest(clientId, "SHOWTAKENLOANS --help");
        String result2 = test.processRequest(clientId, "SHOWTAKENLOANS -h");

        Assertions.assertEquals("SHOWTAKENLOANS\t\t\t\t\t\t\t\t\t\tShows all taken loans of the current customer\n", result);
        Assertions.assertEquals("SHOWTAKENLOANS\t\t\t\t\t\t\t\t\t\tShows all taken loans of the current customer\n", result2);
    }

    @Test
    public void speedTest() {
        Customer testCustomer = new Customer();
        Account account = new Account("23232323", "Current", 1000);
        testCustomer.addAccount(account);
        CustomerID clientId = new CustomerID("TestID152");
        test.addCustomer(testCustomer, clientId.getKey());

        for (int i=0; i<500; i++) {
            test.processRequest(clientId, "OFFERLOAN 1 12345678 365 5");
        }
    }

}

