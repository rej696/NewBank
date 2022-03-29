package newbank.server;

import java.time.LocalDate;
import java.util.function.Supplier;
import java.lang.Math;

public class Loan {
    public static Supplier<LocalDate> currentTime = LocalDate::now;
    
    private double amount;
    private double initialAmount;
    private Account accountFrom;
    private Account accountTo;
    private int termDays;
    private int interest;
    private int number;
    private LocalDate timeOfLoanIssue;
    private LocalDate timeOfLastInterestPayment;
    private boolean openLoan;

    public Loan(double amount, Account accountFrom, int termDays, int interest, int number) {
        this.amount = amount;
        this.initialAmount = amount;
        this.accountFrom = accountFrom;
        this.termDays = termDays;
        this.interest = interest;
        this.number = number;
        this.timeOfLoanIssue = currentTime.get().plusDays(0);
        this.timeOfLastInterestPayment = currentTime.get().plusDays(0);
        this.openLoan = true;
    }

    public boolean isLoanOpen() {
        return openLoan;
    }

    public void setLoanOpenStatus(boolean status) {
        this.openLoan = status;
    }

    public double getDailyInterest() {
        return (this.amount * (((double) this.interest) / 100.0)) / 365.0;
    }

    public double getInitialAmount() {
        return this.initialAmount;
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

    public LocalDate getDateOfLoanIssue() {
        return timeOfLoanIssue;
    }

    public int getLoanDaysElapsed() {
        return this.timeOfLoanIssue.compareTo(currentTime.get());
    }

    public int getLoanDaysRemaining() {
        return this.termDays - getLoanDaysElapsed();
    }

    public int getLoanLengthMonths() {
        return this.termDays / 30;
    }

    public double getDailyInterestRate() {
        return (double) this.interest / 365.0;
    }

    public double getInitialMonthlyLoanPaymentValue() {
        // A = P (r (1+r)^n) / ( (1+r)^n -1 )
        // A is payment value per period (day)
        // P is principal loan value
        // r is interest rate per period (day)
        // n is total number of periods (loan length in days)

        double P = this.getInitialAmount();
        double r = this.getDailyInterestRate() / 100;
        double n = this.termDays;
        double A = P * (r * Math.pow((1+r),n)/(Math.pow((1+r),n) - 1));

        return A * 30;
    }

    public double getCurrentMonthlyLoanPaymentValue() {
        // A = P (r (1+r)^n) / ( (1+r)^n -1 )
        // A is payment value per period (day)
        // P is principal loan value
        // r is interest rate per period (day)
        // n is total number of periods (loan length in days)

        double P = this.amount;
        double r = this.getDailyInterestRate() / 100;
        double n = this.getLoanDaysRemaining();
        double A = P * (r * Math.pow((1+r),n)/(Math.pow((1+r),n) - 1));

        return A * 30;
    }
}
