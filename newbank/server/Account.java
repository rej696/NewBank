package newbank.server;

import java.io.Serializable;

public class Account implements Serializable {

    private final String accountNumber;
    private final String accountName;
    private double balance;
    private double frozenAmount;

    public Account(String accountNumber, String accountName, double openingBalance) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = openingBalance;
        this.frozenAmount = 0;
    }

    public boolean credit(double amount) {
        balance += amount;
        return true;
    }

    public boolean debit(double amount) {
        if (balance < amount) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public String toString() {
        return (accountNumber + " " + accountName + ": " + balance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() { return this.balance; }

    public double getAvailableBalance() { return this.balance - this.frozenAmount; }

    public void setFrozenAmount(double amount) {
        this.frozenAmount += amount;
    }
}
