package newbank.server;

public class Loan {
    double amount;
    Account accountFrom;
    Account accountTo;
    int termDays;
    int interest;

    public Loan(double amount, Account accountFrom, int termDays, int interest) {
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.termDays = termDays;
        this.interest = interest;
    }

}
