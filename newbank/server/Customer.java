package newbank.server;

import java.util.ArrayList;

public class Customer {

    private final ArrayList<Account> accounts;

    public Customer() {
        accounts = new ArrayList<>();
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
