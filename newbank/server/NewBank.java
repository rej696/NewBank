package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class NewBank {

    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;
    private ArrayList<Loan> loans;
    private int lastLoanNumber;

    private NewBank() {
        customers = new HashMap<>();
        loans = new ArrayList<>();
        addTestData();
        lastLoanNumber = 0;
    }

    public static NewBank getBank() {
        return bank;
    }

    public void addCustomer(Customer customer, String customerID) {
        this.customers.put(customerID, customer);
    }

    private void addTestData() {
        Customer kim = new Customer();
        kim.addAccount(new Account(generateAccountNumber(), "Checking", 1050.0));
        customers.put("Kim", kim);

        Customer andy = new Customer();
        andy.addAccount(new Account(generateAccountNumber(), "Main", 3000.0));
        customers.put("Andy", andy);

        Customer rowan = new Customer();
        rowan.addAccount(new Account(generateAccountNumber(), "Savings", 1500.0));
        customers.put("Rowan", rowan);

        Customer damian = new Customer();
        damian.addAccount(new Account(generateAccountNumber(), "Savings", 2050.0));
        customers.put("Damian", damian);

        Customer thomas = new Customer();
        thomas.addAccount(new Account(generateAccountNumber(), "Savings", 1550.0));
        customers.put("Thomas", thomas);

        Customer tester = new Customer();
        tester.addAccount(new Account(generateAccountNumber(), "Main", 1000.00));
        customers.put("tester", tester);

        Customer tester2 = new Customer();
        customers.put("tester2", tester2);

    }

    public Customer getCustomer(CustomerID customerID) {
        return customers.get(customerID.getKey());
    }

    private ArrayList<Customer> getAllCustomers(){
        ArrayList<Customer> customersArrayList = new ArrayList<Customer>();
        for(Customer c : customers.values()){
            customersArrayList.add(c);
        }
        return customersArrayList;
    }

    private ArrayList<Account> getAllAccounts(){
        ArrayList<Customer> allCustomers = getAllCustomers();
        ArrayList<Account> allAccounts = new ArrayList<Account>();
        ArrayList<Account> customerAccounts = new ArrayList<Account>();
        for (Customer c : allCustomers){
            customerAccounts = c.getAllAccounts();
            for (Account a : customerAccounts){
                allAccounts.add(a);
            }
        }
        return allAccounts;
    }

    private ArrayList<String> getAllAccountNumbers(){
        ArrayList<Account> allAccounts = getAllAccounts();
        ArrayList<String> allAccountNumbers = new ArrayList<String>();
        for (Account a : allAccounts){
            allAccountNumbers.add(a.getAccountNumber());
        }
        return allAccountNumbers;
    }

    public synchronized CustomerID checkLogInDetails(String userName, String password) {
        if (customers.containsKey(userName)) {
            return new CustomerID(userName);
        }
        return null;
    }

    // commands from the NewBank customer are processed in this method
    public synchronized String processRequest(CustomerID customer, String request) {
        if (customers.containsKey(customer.getKey())) {
            String[] stringInputs = request.split(" ");
            String command = stringInputs[0];

            switch (command) {
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);
                case "NEWACCOUNT": {
                    if (stringInputs.length > 1) {
                        String name = stringInputs[1];
                        return createAccount(customer, name);
                    }
                }
                case "MOVE": {
                    if (stringInputs.length > 3) {
                        return moveFundsBetweenAccounts(customer, Double.parseDouble(stringInputs[1]), stringInputs[2], stringInputs[3], true);
                    }
                }
                case "PAY": {
                    if (stringInputs.length > 3) {
                        return moveFundsBetweenAccounts(customer, Double.parseDouble(stringInputs[1]), stringInputs[2], stringInputs[3], false);
                    }
                }
                case "OFFERLOAN": {
                    if (stringInputs.length > 4) {
                        return offerLoan(Double.parseDouble(stringInputs[1]), stringInputs[2], Integer.parseInt(stringInputs[3]), Integer.parseInt(stringInputs[4]));
                    }
                }
                case "SHOWMYOFFEREDLOANS": {
                        return showMyOfferedLoans(customer);
                }
                default:
                    return "FAIL";
            }
        }
        return "FAIL";
    }

    private String showMyOfferedLoans(CustomerID customerID) {
        Customer customer = this.getCustomer(customerID);
        String result = "";

        for (Account account : customer.getAllAccounts()) {
            for(Loan loan: loans){
                if(loan.accountFrom.getAccountNumber().equals(account.getAccountNumber())){
                  result = result + "Loan Number: "+ loan.number +", Account Number: "+ loan.accountFrom.getAccountNumber() +", Amount: " + loan.amount + ", Interest Rate: " + loan.interest + "%\n";
                }
            }
        }
        return result == "" ? "No loans offered" : result;
    }

    private String offerLoan(double amount, String accountNumber, int term, int interest) {
        Account account = getAccount(accountNumber);
        if(account == null) {
            return "ERROR. Loan of " + amount + " cannot be offered from " + accountNumber + ". Account is non-existent.";
        }
        if(amount > account.getAvailableBalance()) {
            return "ERROR. Loan of " + amount + " cannot be offered from " + accountNumber + ". Loan amount exceeds funds.";
        }
        if(interest > 10 || interest < 0) {
            return "ERROR. Loan of " + amount + " cannot be offered from " + accountNumber + " with interest " + interest + "%. Interest is too high.";
        }
        this.lastLoanNumber += 1;
        Loan newLoan = new Loan(amount, getAccount(accountNumber), term, interest, this.lastLoanNumber);
        account.setFrozenAmount(amount);
        loans.add(newLoan);
        return "Success. Loan of " + amount + " offered from " + accountNumber + " for " + term + " days with interest of " + interest + "%";
    }

    private String moveFundsBetweenAccounts(CustomerID customerID, double amount, String fromAccountNumber, String toAccountNumber, boolean accountsBelongToSameCustomer) {
        Customer customer = this.getCustomer(customerID);
        Account account1 = customer.getAccount(fromAccountNumber);
        Account account2 = accountsBelongToSameCustomer ? customer.getAccount(toAccountNumber) : getAccount(toAccountNumber);

        if (account1 == null || account2 == null) {
            return "Account number invalid";
        }

        if (!account1.debit(amount)) {
            return "Insufficient funds";
        }

        account2.credit(amount);
        return accountsBelongToSameCustomer
                ? "Success. " + amount + " moved from " + fromAccountNumber + " to " + toAccountNumber + "\n\nNew Balance\n\n" + account1 + "\n" + account2
                : "Success. " + amount + " paid from " + fromAccountNumber + " to " + toAccountNumber + "\n\nNew Balance\n\n" + account1;
    }

    private Account getAccount(String accountNumber) {
        for (String key : this.customers.keySet()) {
            for (Account a : this.customers.get(key).getAllAccounts()) {
                if (accountNumber.equals(a.getAccountNumber())) {
                    return a;
                }
            }
        }
        return null;
    }


    private String createAccount(CustomerID customerId, String name) {

        if (isValidAccountName(name)) {
            Customer customer = this.getCustomer(customerId);
            customer.addAccount(new Account(generateAccountNumber(), name, 0.00));

            return "Account with name " + name + " was created";
        } else {
            return "Account not created. Illegal account name";
        }
    }

    private boolean isValidAccountName(String name) {
        if (name.length() > 30) {
            return false;
        }

        for (char c : name.toCharArray()) {
            if (Character.isDigit(c) || Character.isAlphabetic(c) || c == '-') {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private String showMyAccounts(CustomerID customer) {
        String accounts = (customers.get(customer.getKey())).accountsToString();

        if (accounts == "") {
            return "ERROR: Customer " + customer.getKey() + " has no accounts";
        }
        return accounts;
    }

    private String generateAccountNumber() {

        String numberAsString;
        ArrayList<String> accountNumbers = getAllAccountNumbers();

        do {
            int number = (int) Math.floor(Math.random() * (99999999 - 1 + 1) + 1);
            numberAsString = String.valueOf(number);
            int difference = 8 - numberAsString.length();

            for (int i = 0; i < difference; i++) {
                numberAsString = "0" + numberAsString;
            }
        } while (accountNumbers.contains(numberAsString));

        accountNumbers.add(numberAsString);
        return numberAsString;
    }

}
