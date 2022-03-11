package newbank.server;

public class Loan {
    double amount;
    Account accountFrom;
    Account accountTo;
    int termDays;
    int interest;
    int number;

    public Loan(double amount, Account accountFrom, int termDays, int interest, int number) {
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.termDays = termDays;
        this.interest = interest;
        this.number = number;
    }

}
