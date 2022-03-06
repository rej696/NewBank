package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private final HashMap<String,Customer> customers;
	private final ArrayList<String> accountNumbers = new ArrayList<>();
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	public void addCustomer(Customer customer, String customerID) {
		this.customers.put(customerID, customer);
	}
	
	private void addTestData() {
		Customer kim = new Customer();
		kim.addAccount(new Account(generateAccountNumber(), "Checking", 1050.0));
		customers.put("Kim", kim);

		Customer andy = new Customer();
		andy.addAccount(new Account(generateAccountNumber(),"Main", 3000.0));
		customers.put("Andy", andy);
		
		Customer rowan = new Customer();
		rowan.addAccount(new Account(generateAccountNumber(),"Savings", 1500.0));
		customers.put("Rowan", rowan);

		Customer damian = new Customer();
		damian.addAccount(new Account(generateAccountNumber(),"Savings", 2050.0));
		customers.put("Damian", damian);

		Customer thomas = new Customer();
		thomas.addAccount(new Account(generateAccountNumber(),"Savings", 1550.0));
		customers.put("Thomas", thomas);

		Customer tester = new Customer();
		tester.addAccount(new Account(generateAccountNumber(),"Main", 1000.00));
		customers.put("tester", tester);

		Customer tester2 = new Customer();
		customers.put("tester2", tester2);

	}
	
	public static NewBank getBank() {
		return bank;
	}

	public Customer getCustomer(CustomerID customerID) {return customers.get(customerID.getKey());}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			String[] stringInputs = request.split(" ");
			String command = stringInputs[0];

			switch(command) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
				case "NEWACCOUNT" : {
					if (stringInputs.length > 1) {
						String name = stringInputs[1];
						return createAccount(customer, name);
					}
				}
				case "MOVE": {
					if (stringInputs.length > 3) {
						return moveFunds(customer, Double.parseDouble(stringInputs[1]),stringInputs[2],stringInputs[3]);
					}
				}
				case "PAY": {
					if (stringInputs.length > 3) {
						return makePayment(customer, Double.parseDouble(stringInputs[1]),stringInputs[2],stringInputs[3]);
					}
				}
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private String makePayment(CustomerID customerID, double amount, String fromAccountNumber, String toAccountNumber) {
		Customer customer = this.getCustomer(customerID);
		Account account1 = customer.getAccount(fromAccountNumber);
		Account account2 = getAccount(toAccountNumber);
		if(account1 == null || account2 == null){
			return "Account number invalid";
		}
		if(!account1.debit(amount)){
			return "Insufficient funds";
		}
		account2.credit(amount);
		return "Success. " + amount + " payed from " + fromAccountNumber + " to " + toAccountNumber + "\n\nNew Balance\n\n" + account1;
	}

	private Account getAccount(String accountNumber) {
		for (String key : this.customers.keySet()) {
			for(Account a : this.customers.get(key).getAllAccounts()) {
				String accountNumber1 = a.getAccountNumber();
				if (accountNumber1.equals(accountNumber)) {
					return a;
				}
			}
		}
		return null;
	}

	private String moveFunds(CustomerID customerID, double amount, String fromAccountNumber, String toAccountNumber) {
		Customer customer = this.getCustomer(customerID);
		Account account1 = customer.getAccount(fromAccountNumber);
		Account account2 = customer.getAccount(toAccountNumber);
		if(account1 == null || account2 == null){
			return "Account number invalid";
		}
		if(!account1.debit(amount)){
			return "Insufficient funds";
		}
		account2.credit(amount);
		return "Success. " + amount + " moved from " + fromAccountNumber + " to " + toAccountNumber + "\n\nNew Balance\n\n" + account1 + "\n" + account2;
	}

	private String createAccount(CustomerID customerId, String name) {

		if(isValidAccountName(name)) {
			Customer customer = this.getCustomer(customerId);
			customer.addAccount(new Account(generateAccountNumber(), name, 0.00));

			return "Account with name " + name + " was created";
		}else{
			return "Account not created. Illegal account name";
		}
		}

	private boolean isValidAccountName(String name) {
		if(name.length() > 30){
			return false;
		}

		for (char c: name.toCharArray()) {
			if(Character.isDigit(c) || Character.isAlphabetic(c) || c == '-'){
			}else {
				return false;
			}
		}
		return true;
	}

	private String showMyAccounts(CustomerID customer) {
		String accounts = (customers.get(customer.getKey())).accountsToString();

		if(accounts == ""){
			return "Error customer has no accounts";
		}
		return accounts;
	}

	private String generateAccountNumber() {

		String numberAsString;

		do{
			int number = (int) Math.floor(Math.random()*(99999999-1+1)+1);
			numberAsString = String.valueOf(number);
			int difference = 8 - numberAsString.length();

			for (int i = 0; i < difference; i++){
				numberAsString = "0" + numberAsString;
			}
		}while(accountNumbers.contains(numberAsString));

		accountNumbers.add(numberAsString);
		return numberAsString;
	}

}
