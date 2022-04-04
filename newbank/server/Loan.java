package newbank.server;

import java.time.LocalDate;
import java.util.function.Supplier;
import java.io.Serializable;
import java.lang.Math;

public class Loan implements Serializable {
    public static Supplier<LocalDate> currentTime = LocalDate::now;
    
    private double amount;
    private double initialAmount;
    private Account accountFrom;
    private Account accountTo;
    private int termDays;
    private int interest;
    private int number;
    private double interestAmount;
    private LocalDate timeOfLoanIssue;
    private LocalDate timeOfLastInterestPayment;
    private LocalDate interestDueDate;
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
        this.interestDueDate = currentTime.get().plusDays(365);
        this.openLoan = true;
        this.interestAmount = 0;
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
        return amount;
    }

    public double getAmountWithInterest(){
        LocalDate now = currentTime.get();
        return this.getAmountWithIntrestToDate(now);
    }

    private double getAmountWithIntrestToDate(LocalDate date){
        for (LocalDate i = this.timeOfLastInterestPayment; i.isBefore(date); i = i.plusDays(1)) {
            double dailyInterest = getDailyInterest();
            this.interestAmount += dailyInterest;

            if(interestDueDate == i){
                this.interestDueDate = interestDueDate.plusDays(365);
                this.amount += this.interestAmount;
                this.interestAmount = 0;
            }
        }
        this.timeOfLastInterestPayment = date;
        return this.amount + this.interestAmount;
    }

    public double getEndAmount(){
        return this.getAmountWithIntrestToDate(this.timeOfLoanIssue.plusDays(this.termDays));
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
