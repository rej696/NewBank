package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelpTest extends test.MainTest {

    @Test
    public void getHelp() {
        String result = test.processRequest(this.customerId1, "HELP");
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
                "PARTPAYBACKLOAN <Loan Number> <Amount>\t\t\t\t\t\t\tThe amount is paid off the loan balance\n" +
                "SHOWTAKENLOANS\t\t\t\t\t\t\t\t\t\tShows all taken loans of the current customer\n" +
                "MAKEMONTHLYPAYMENT <Loan Number>\t\t\t\t\t\t\tMakes the value of monthly payment against the loan number required to pay off the loan evenly over the loan period\n" +
                "HELP\t\t\t\t\t\t\t\t\t\t\t\tShows this menu\n\n", result);
    }

    @Test
    public void getHelpShowMyAccounts() {
        String result = test.processRequest(this.customerId1, "SHOWMYACCOUNTS --help");
        String result2 = test.processRequest(this.customerId1, "SHOWMYACCOUNTS -h");

        Assertions.assertEquals("SHOWMYACCOUNTS\t\t\t\t\t\t\t\t\t\tShows all of the current customer's account details\n", result);
        Assertions.assertEquals("SHOWMYACCOUNTS\t\t\t\t\t\t\t\t\t\tShows all of the current customer's account details\n", result2);
    }

    @Test
    public void getHelpNewAccount() {
        String result = test.processRequest(this.customerId1, "NEWACCOUNT --help");
        String result2 = test.processRequest(this.customerId1, "NEWACCOUNT -h");

        Assertions.assertEquals("NEWACCOUNT <New account name>\t\t\t\t\t\tCreates a new account for the current customer with the specified name\n", result);
        Assertions.assertEquals("NEWACCOUNT <New account name>\t\t\t\t\t\tCreates a new account for the current customer with the specified name\n", result2);
    }

    @Test
    public void getHelpMove() {
        String result = test.processRequest(this.customerId1, "MOVE --help");
        String result2 = test.processRequest(this.customerId1, "MOVE -h");

        Assertions.assertEquals("MOVE <Amount> <Debit account> <Credit account>\t\tMoves the amount specified between two of a customer's accounts\n", result);
        Assertions.assertEquals("MOVE <Amount> <Debit account> <Credit account>\t\tMoves the amount specified between two of a customer's accounts\n", result2);
    }

    @Test
    public void getHelpPay() {
        String result = test.processRequest(this.customerId1, "PAY --help");
        String result2 = test.processRequest(this.customerId1, "PAY -h");

        Assertions.assertEquals("PAY <Amount> <Debit account> <Credit account>\t\tPays funds from one account to another account, which may be held by another customer\n", result);
        Assertions.assertEquals("PAY <Amount> <Debit account> <Credit account>\t\tPays funds from one account to another account, which may be held by another customer\n", result2);
    }

    @Test
    public void getHelpOfferLoan() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN --help");
        String result2 = test.processRequest(this.customerId1, "OFFERLOAN -h");

        Assertions.assertEquals("OFFERLOAN <Amount> <FromAccount> <Terms> <intrest>\tCreates a loan for the specified period, under the defined conditions\n", result);
        Assertions.assertEquals("OFFERLOAN <Amount> <FromAccount> <Terms> <intrest>\tCreates a loan for the specified period, under the defined conditions\n", result2);
    }

    @Test
    public void getHelpShowMyOfferedLoans() {
        String result = test.processRequest(this.customerId1, "SHOWMYOFFEREDLOANS --help");
        String result2 = test.processRequest(this.customerId1, "SHOWMYOFFEREDLOANS -h");

        Assertions.assertEquals("SHOWMYOFFEREDLOANS\t\t\t\t\t\t\t\t\tShows all offered loans of the current customer\n", result);
        Assertions.assertEquals("SHOWMYOFFEREDLOANS\t\t\t\t\t\t\t\t\tShows all offered loans of the current customer\n", result2);
    }

    @Test
    public void getHelpShowOpenLoans() {
        String result = test.processRequest(this.customerId1, "SHOWOPENLOANS --help");
        String result2 = test.processRequest(this.customerId1, "SHOWOPENLOANS -h");

        Assertions.assertEquals("SHOWOPENLOANS\t\t\t\t\t\t\t\t\t\tShows all open loans with the conditions of the loan.\n", result);
        Assertions.assertEquals("SHOWOPENLOANS\t\t\t\t\t\t\t\t\t\tShows all open loans with the conditions of the loan.\n", result2);
    }

    @Test
    public void getHelpAcceptLoan() {
        String result = test.processRequest(this.customerId1, "ACCEPTLOAN --help");
        String result2 = test.processRequest(this.customerId1, "ACCEPTLOAN -h");

        Assertions.assertEquals("ACCEPTLOAN <Loan Number> <Account>\t\t\t\t\tThe open loan is accepted and the amount is credited to the given account.\n", result);
        Assertions.assertEquals("ACCEPTLOAN <Loan Number> <Account>\t\t\t\t\tThe open loan is accepted and the amount is credited to the given account.\n", result2);
    }

    @Test
    public void getHelpPayBackLoan() {
        String result = test.processRequest(this.customerId1, "PAYBACKLOAN --help");
        String result2 = test.processRequest(this.customerId1, "PAYBACKLOAN -h");

        Assertions.assertEquals("PAYBACKLOAN <Loan Number>\t\t\t\t\t\t\tThe loan is repaid with interest\n", result);
        Assertions.assertEquals("PAYBACKLOAN <Loan Number>\t\t\t\t\t\t\tThe loan is repaid with interest\n", result2);
    }

    @Test
    public void getHelpShowTakenLoans() {
        String result = test.processRequest(this.customerId1, "SHOWTAKENLOANS --help");
        String result2 = test.processRequest(this.customerId1, "SHOWTAKENLOANS -h");

        Assertions.assertEquals("SHOWTAKENLOANS\t\t\t\t\t\t\t\t\t\tShows all taken loans of the current customer\n", result);
        Assertions.assertEquals("SHOWTAKENLOANS\t\t\t\t\t\t\t\t\t\tShows all taken loans of the current customer\n", result2);
    }
}
