package newbank.server;

import java.time.LocalDate;
import java.util.function.Supplier;

public class Loan {
    public static Supplier<LocalDate> currentTime = LocalDate::now;
    
    private double amount;
    private Account accountFrom;
    private Account accountTo;
    private int termDays;
    private int interest;
    private int number;
    private LocalDate timeOfLastInterestPayment;

    public Loan(double amount, Account accountFrom, int termDays, int interest, int number) {
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.termDays = termDays;
        this.interest = interest;
        this.number = number;
        this.timeOfLastInterestPayment = currentTime.get().plusDays(0);
    }

    public double getDailyInterest() {
        return (this.amount * (((double) this.interest) / 100.0)) / 365.0;
    }

    public double getAmount() {
        LocalDate now = currentTime.get();
        for (LocalDate i = this.timeOfLastInterestPayment; i.isBefore(now); i = i.plusDays(1)) {
          double dailyInterest = getDailyInterest();
          this.amount += dailyInterest;
        }
        this.timeOfLastInterestPayment = now;
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

    public void pay(double value) {
        this.amount -= value;
    }
}
