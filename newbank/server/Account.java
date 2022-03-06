package newbank.server;

import java.time.LocalDateTime;
import java.util.Random;

public class Account {

	private String accountNumber;
	private String accountName;
	private double openingBalance;

	public Account(String accountNumber, String accountName, double openingBalance) {
		this.accountNumber = accountNumber;
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}

	public String toString() {
		return (accountNumber + " " + accountName + ": " + openingBalance);
	}

}
