package newbank.server;

import java.util.HashMap;

public class LoanManager {
  private static final LoanManager loanManager = new LoanManager();
  private static final NewBankQuery_I bank = NewBank.getBank();

  private HashMap<Integer, Loan> loans;
  private int lastLoanNumber;

  private LoanManager() {
    loans = new HashMap<>();
    lastLoanNumber = 0;
  }

  public static LoanManager getLoanManager() {
    return loanManager;
  }

  public void clearLoans() {
    this.loans.clear();
    lastLoanNumber = 0;
  }
  
  public String showTakenLoans(CustomerID customerID) {
    Customer customer = bank.getCustomer(customerID);
    String result = "";

    for (Account account : customer.getAllAccounts()) {
      for (int loanID : loans.keySet()) {
        Loan loan = loans.get(loanID);
        if (loan.getAccountTo() != null
            && loan.getAccountTo().getAccountNumber().equals(account.getAccountNumber())) {
          result = result + "Loan Number: " + loan.getNumber() + ", Account Number: "
              + loan.getAccountFrom().getAccountNumber() + ", Amount: " + loan.getAmount()
              + ", Interest Rate: " + loan.getInterest() + "%, Taken by: "
              + loan.getAccountTo().getAccountNumber() + "\n";
        }
      }
    }
    return result == "" ? "No loans taken" : result;
  }

  public String paybackLoan(CustomerID customerID, int loanNumber) {
    Loan loan = loans.get(loanNumber);
    if (loan == null) {
      return "Error. Invalid loan number.";
    } else {
      Account accountTo = loan.getAccountFrom();
      Account accountFrom = loan.getAccountTo();
      if (accountTo == null) {
        return "Error. Invalid account number.";
      }
      if (accountFrom == null) {
        return "Error. Invalid account number.";
      }

      return handleLoanPayment(loan, loan.getAmount());
    }
  }

  public String paybackLoanPartial(CustomerID customerID, int loanNumber, double value) {
    Loan loan = loans.get(loanNumber);
    if (loan == null) {
      return "Error. Invalid loan number.";
    } else {
      Account accountTo = loan.getAccountFrom();
      Account accountFrom = loan.getAccountTo();
      if (accountTo == null) {
        return "Error. Invalid account number.";
      }
      if (accountFrom == null) {
        return "Error. Invalid account number.";
      }

      return handleLoanPayment(loan, value);
    }
  }

  public String payMonthlyPayment(CustomerID customerID, int loanNumber){
      return paybackLoanPartial(customerID, loanNumber, loans.get(loanNumber).getCurrentMonthlyLoanPaymentValue());
  }

  private String handleLoanPayment(Loan loan, double value) {
    Account accountTo = loan.getAccountFrom();
    Account accountFrom = loan.getAccountTo();
    double paymentAmount = value;
    double loanAmount = loan.getAmount();

    if (value <= loanAmount) {
      loan.pay(value);
    } else {
      loan.setLoanOpenStatus(false);
      paymentAmount = loanAmount;
    }

    if (paymentAmount > accountFrom.getAvailableBalance()) {
      return "Error. Insufficient funds.";
    }

    accountFrom.debit(paymentAmount);
    accountTo.credit(paymentAmount);
    // accountTo.setFrozenAmount(paymentAmount * -1);
    return "Success. Loan Number: " + loan.getNumber() + ", Account Number From: "
        + accountFrom.getAccountNumber() + ", Account Number To: " + accountTo.getAccountNumber()
        + ", Amount: " + String.format("%.2f", paymentAmount) + "\n";
  }

  public String acceptLoan(CustomerID customerID, int loanNumber, String accountTo) {
    Customer customer = bank.getCustomer(customerID);
    Account account = customer.getAccount(accountTo);
    Loan loan = loans.get(loanNumber);
    if (account == null) {
      return "Error. Invalid account number.";
    }
    if (loan == null) {
      return "Error. Invalid loan number.";
    } else {
      loan.setAccountTo(account);
      account.credit(loan.getAmount());
      loan.getAccountFrom().setFrozenAmount(0);
      return "Success. Loan number " + loan.getNumber() + " accepted by account "
          + loan.getAccountTo().getAccountNumber() + ".";
    }
  }
 
  public String showOpenLoans(CustomerID customerID) {
    Customer customer = bank.getCustomer(customerID);
    String result = "";

    for (Account account : customer.getAllAccounts()) {
      for (int loanID : loans.keySet()) {
        Loan loan = loans.get(loanID);
        if (!loan.getAccountFrom().getAccountNumber().equals(account.getAccountNumber())) {
          result = result + "Loan Number: " + loan.getNumber() + ", Amount: " + String.format("%.2f",loan.getAmount())
              + ", Term: " + loan.getTermDays() + " days, Interest Rate: " + loan.getInterest()
              + "%\n";
        }
      }
    }
    return result == "" ? "No loans available at present" : result;
  }


  public String showMyOfferedLoans(CustomerID customerID) {
    Customer customer = bank.getCustomer(customerID);
    String result = "";

    for (Account account : customer.getAllAccounts()) {
      for (int loanID : loans.keySet()) {
        Loan loan = loans.get(loanID);
        if (loan != null && loan.getAccountFrom().getAccountNumber().equals(account.getAccountNumber())) {
          result = result + "Loan Number: " + loan.getNumber() + ", Account Number: "
              + loan.getAccountFrom().getAccountNumber() + ", Amount: " + loan.getAmount()
              + ", Interest Rate: " + loan.getInterest() + "%" + (loan.getAccountTo() == null ? ""
                  : ", Taken by: " + loan.getAccountTo().getAccountNumber())
              + "\n";
        }
      }
    }
    return result == "" ? "No loans offered" : result;
  }

  public String offerLoan(double amount, String accountNumber, int term, int interest) {
    Account account = bank.getAccount(accountNumber);
    if (account == null) {
      return "ERROR. Loan of " + amount + " cannot be offered from " + accountNumber
          + ". Account is non-existent.";
    }

    if (amount > account.getAvailableBalance()) {
      return "ERROR. Loan of " + amount + " cannot be offered from " + accountNumber
          + ". Loan amount exceeds funds.";
   }

    if (interest > 10 || interest < 0) {
      return "ERROR. Loan of " + amount + " cannot be offered from " + accountNumber
          + " with interest " + interest + "%. Interest is too " + (interest > 0 ? "high" : "low") + ".";
    }

    this.lastLoanNumber += 1;
    account.setFrozenAmount(amount);
    loans.put(lastLoanNumber,
        new Loan(amount, bank.getAccount(accountNumber), term, interest, this.lastLoanNumber));
    
    return "Success. Loan of " + amount + " offered from " + accountNumber + " for " + term
        + " days with interest of " + interest + "%";
  }

}
