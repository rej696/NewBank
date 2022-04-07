package newbank.server;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {

    private final ArrayList<Account> accounts;
    private String password;
    private int incorrectPasswordAttempts;

    public Customer(String password) {
        accounts = new ArrayList<>();
        this.incorrectPasswordAttempts = 0;
        this.password = password;
    }

    public Customer() {
        accounts = new ArrayList<>();
        this.password = "";
    }

    public boolean correctPassword(String password) {
        if (password.equals(this.password) && incorrectPasswordAttempts < 4){
            incorrectPasswordAttempts = 0;
            return true;
        }
        else {
            incorrectPasswordAttempts++;
            return false;
        }
    }

    public String accountsToString() {
        String s = "";
        for (Account a : accounts) {
            s += a.toString() + "\n";
        }
        return s;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Account getAccount(String accountNumber) {
        for (Account account : accounts) {
            String accountNumber1 = account.getAccountNumber();
            if (accountNumber1.equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public ArrayList<Account> getAllAccounts() {
        return this.accounts;
    }
}
