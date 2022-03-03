package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer kim = new Customer();
		kim.addAccount(new Account("Checking", 1050.0));
		customers.put("Kim", kim);

		Customer andy = new Customer();
		andy.addAccount(new Account("Main", 3000.0));
		customers.put("Andy", andy);
		
		Customer rowan = new Customer();
		rowan.addAccount(new Account("Savings", 1500.0));
		customers.put("Rowan", rowan);

		Customer damian = new Customer();
		damian.addAccount(new Account("Savings", 2050.0));
		customers.put("Damian", damian);

		Customer thomas = new Customer();
		thomas.addAccount(new Account("Savings", 1550.0));
		customers.put("Thomas", thomas);

		Customer tester = new Customer();
		tester.addAccount(new Account("Main", 1000.00));
		customers.put("tester", tester);

	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
