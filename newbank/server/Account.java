package newbank.server;

public class Account {

    private final String accountNumber;
    private final String accountName;
    private double balance;

    public Account(String accountNumber, String accountName, double openingBalance) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = openingBalance;
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
}
