package newbank.server;

public class Loan {
    private double amount;
    private Account accountFrom;
    private Account accountTo;
    private int termDays;
    private int interest;
    private int number;

    public Loan(double amount, Account accountFrom, int termDays, int interest, int number) {
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.termDays = termDays;
        this.interest = interest;
        this.number = number;
    }


    public double getAmount() {
        return amount;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public int getTermDays() {
        return termDays;
    }

    public int getInterest() {
        return interest;
    }

    public int getNumber() {
        return number;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
    }

    public Account getAccountTo() {
        return accountTo;
    }
}
