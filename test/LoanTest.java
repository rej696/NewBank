package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class LoanTest extends test.MainTest {

    @Test
    public void offerLoan() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");
        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
    }

    @Test
    public void offerLoanInsufficientFunds() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 1001 12345678 365 5");
        Assertions.assertEquals("ERROR. Loan of 1001.0 cannot be offered from 12345678. Loan amount exceeds funds.", result);
    }

    @Test
    public void offerLoanInterestNotInRange() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 11");
        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 12345678 with interest 11%. Interest is too high.", result);
    }

    @Test
    public void offerLoanNotExistingAccountNumber() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 00000000 365 5");
        Assertions.assertEquals("ERROR. Loan of 500.0 cannot be offered from 00000000. Account is non-existent.", result);
    }

    @Test
    public void offerLoanFreezeLoanAmount() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");
        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());
    }

    @Test
    public void showMyOfferLoans() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result2);
        Assertions.assertEquals(0, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result3 = test.processRequest(this.customerId1, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("Loan Number: 1, Account Number: 12345678, Amount: 500.0, Interest Rate: 5%\nLoan Number: 2, Account Number: 12345678, Amount: 500.0, Interest Rate: 5%\n", result3);
    }

    @Test
    public void showMyOfferedLoansNoLoansOffered() {
        String result = test.processRequest(this.customerId1, "SHOWMYOFFEREDLOANS");
        Assertions.assertEquals("No loans offered", result);
    }

    @Test
    public void ShowOpenLoans() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result2);
        Assertions.assertEquals(0, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result3 = test.processRequest(this.customerId2, "SHOWOPENLOANS");

        Assertions.assertEquals("Loan Number: 1, Amount: 500.00, Term: 365 days, Interest Rate: 5%\nLoan Number: 2, Amount: 500.00, Term: 365 days, Interest Rate: 5%\n", result3);
    }

    @Test
    public void ShowOpenLoansNoLoansAvailable() {
        String result = test.processRequest(this.customerId2, "SHOWOPENLOANS");
        Assertions.assertEquals("No loans available at present", result);
    }

    @Test
    public void acceptLoan() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);
        Assertions.assertEquals(1500, this.customer2.getAllAccounts().get(0).getAvailableBalance());
    }

    @Test
    public void acceptLoanInvalidLoanNumber() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 2 23456789");

        Assertions.assertEquals("Error. Invalid loan number.", result2);
    }

    @Test
    public void acceptLoanInvalidAccountNumber() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 00");

        Assertions.assertEquals("Error. Invalid account number.", result2);
    }

    @Test
    public void showMyOfferedAndAcceptedLoans() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);

        String result3 = test.processRequest(this.customerId1, "SHOWMYOFFEREDLOANS");

        Assertions.assertEquals("Loan Number: 1, Account Number: 12345678, Amount: 500.0, Interest Rate: 5%, Taken by: 23456789\n", result3);
    }

    @Test
    public void paybackLoan() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);

        setCurrentTime(LocalDate.of(1996, 3, 14));

        String result3 = test.processRequest(this.customerId1, "PAYBACKLOAN 1");

        Assertions.assertEquals("Success. Loan Number: 1, Account Number From: 23456789, Account Number To: 12345678, Amount: 500.55\n", result3);

        Assertions.assertTrue(this.customer1.getAllAccounts().get(0).getAvailableBalance() > 1000.00);
        Assertions.assertTrue(this.customer2.getAllAccounts().get(0).getAvailableBalance() < 1000.00);
    }

    @Test
    public void invalidLoanNumberPayback() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);

        String result3 = test.processRequest(this.customerId1, "PAYBACKLOAN 2");

        Assertions.assertEquals("Error. Invalid loan number.", result3);
    }

    @Test
    public void insufficientFunds() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);
        this.customer2.getAllAccounts().get(0).debit(1001);
        String result3 = test.processRequest(this.customerId1, "PAYBACKLOAN 1");

        Assertions.assertEquals("Error. Insufficient funds.", result3);
    }

    @Test
    public void showTakenLoans() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);

        String result3 = test.processRequest(this.customerId2, "SHOWTAKENLOANS");

        Assertions.assertEquals("Loan Number: 1, Account Number: 12345678, Amount: 500.0, Interest Rate: 5%, Taken by: 23456789\n", result3);
    }

    @Test
    public void showTakenLoansNoLoansTaken() {
        String result = test.processRequest(this.customerId1, "SHOWTAKENLOANS");
        Assertions.assertEquals("No loans taken", result);
    }

    @Test
    public void partPayBackLoan() {
        String result = test.processRequest(this.customerId1, "OFFERLOAN 500 12345678 365 5");

        Assertions.assertEquals("Success. Loan of 500.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(500, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");

        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);

        String result3 = test.processRequest(this.customerId2, "PARTPAYBACKLOAN 1 50");

        Assertions.assertEquals("Success. Loan Number: 1, Account Number From: 23456789, Account Number To: 12345678, Amount: 50.00\n", result3);

        String result4 = test.processRequest(this.customerId2, "SHOWOPENLOANS");

        Assertions.assertEquals("Loan Number: 1, Amount: 450.00, Term: 365 days, Interest Rate: 5%\n", result4);
    }

    @Test
    public void payMonthlyPayment() {
        this.customer1.getAllAccounts().get(0).credit(1000);
        this.customer2.getAllAccounts().get(0).credit(1000);

        String result = test.processRequest(this.customerId1, "OFFERLOAN 1000 12345678 365 5");
        Assertions.assertEquals("Success. Loan of 1000.0 offered from 12345678 for 365 days with interest of 5%", result);
        Assertions.assertEquals(1000, this.customer1.getAllAccounts().get(0).getAvailableBalance());

        String result2 = test.processRequest(this.customerId2, "ACCEPTLOAN 1 23456789");
        Assertions.assertEquals("Success. Loan number 1 accepted by account 23456789.", result2);

        String result3 = test.processRequest(this.customerId2, "MAKEMONTHLYPAYMENT 1");
        Assertions.assertEquals("Success. Loan Number: 1, Account Number From: 23456789, Account Number To: 12345678, Amount: 84.27\n", result3);

        String result4 = test.processRequest(this.customerId2, "SHOWOPENLOANS");
        Assertions.assertEquals("Loan Number: 1, Amount: 915.73, Term: 365 days, Interest Rate: 5%\n", result4);
    }
}
