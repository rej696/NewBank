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

    public void clearLoans(){
        this.loans.clear();
        lastLoanNumber = 0;
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
                    if (stringInputs.length > 1 && (stringInputs[1].equals("--help") || stringInputs[1].equals("-h"))) {
                        return getHelpShowMyAccounts();
                    } else {
                        return showMyAccounts(customer);
                    }
                case "NEWACCOUNT": {
                    if (stringInputs.length > 1) {
                        if (stringInputs[1].equals("--help") || stringInputs[1].equals("-h")) {
                            return getHelpNewAccount();
                        }
                        String name = stringInputs[1];
                        return createAccount(customer, name);
                    }
                }
                case "MOVE": {
                    if (stringInputs.length == 2 && (stringInputs[1].equals("--help") || stringInputs[1].equals("-h"))) {
                        return getHelpMove();
                    } else if (stringInputs.length > 3) {
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
                case "SHOWOPENLOANS": {
                    return showOpenLoans(customer);
                }
                case "ACCEPTLOAN": {
                    if (stringInputs.length > 2) {
                        return acceptLoan(customer, Integer.parseInt(stringInputs[1]), stringInputs[2]);
                    }
                }
                case "PAYBACKLOAN" : {
                    if (stringInputs.length > 1) {
                        return paybackLoan(customer, Integer.parseInt(stringInputs[1]));
                    }
                }
                case "HELP": {
                    return getHelp();
                }
                default:
                    return "FAIL";
            }
        }
        return "FAIL";
    }

    private String paybackLoan(CustomerID customerID, int loanNumber) {
        Customer customer = this.getCustomer(customerID);
        if(!validLoanNumber(loanNumber)) {
            return "Error. Invalid loan number.";
        }
        for(Loan loan: loans) {
            if (loan.getNumber() == loanNumber) {
                Account accountTo = loan.getAccountFrom();
                Account accountFrom = loan.getAccountTo();
                if(accountTo == null) {
                    return "Error. Invalid account number.";
                }
                if(accountFrom == null) {
                    return "Error. Invalid account number.";
                }
                double amount = loan.getAmount();
                amount += loan.getAmount() / 100 * loan.getInterest();
                if(amount >= accountFrom.getAvailableBalance()) {
                    return "Error. Insufficient funds.";
                }
                accountFrom.debit(amount);
                accountTo.credit(amount);
                accountTo.setFrozenAmount(loan.getAmount() * -1);
                return "Success. Loan Number: " + loan.getNumber() + ", Account Number From: " + accountFrom.getAccountNumber() + ", Account Number To: " + accountTo.getAccountNumber() + ", Amount: " + amount + "\n";
            }
        }
        return "Error. Unable to payback loan.";
    }

    private String acceptLoan(CustomerID customerID, int loanNumber, String accountTo) {
        Customer customer = this.getCustomer(customerID);
        Account account = customer.getAccount(accountTo);
        if(account == null) {
            return "Error. Invalid account number.";
        }
        if(!validLoanNumber(loanNumber)) {
            return "Error. Invalid loan number.";
        }
        for(Loan loan: loans) {
            if (loan.getNumber() == loanNumber) {
                loan.setAccountTo(account);
                account.credit(loan.getAmount());
                return "Success. Loan number " + loan.getNumber() + " accepted by account " + loan.getAccountTo().getAccountNumber() + ".";
            }
        }
        return "Error. Unable to take loan.";
    }

    private boolean validLoanNumber(int loanNumber) {
        for(Loan loan : loans) {
            if(loan.getNumber() == loanNumber) {
                return true;
            }
        }
        return false;
    }

    private String showOpenLoans(CustomerID customerID) {
        Customer customer = this.getCustomer(customerID);
        String result = "";

        for (Account account : customer.getAllAccounts()) {
            for(Loan loan: loans){
                if(!loan.getAccountFrom().getAccountNumber().equals(account.getAccountNumber())){
                    result = result + "Loan Number: "+ loan.getNumber() +", Amount: " + loan.getAmount() + ", Term: " + loan.getTermDays() + " days, Interest Rate: " + loan.getInterest() + "%\n";
                }
            }
        }

        return result == "" ? "No loans available at present" : result;

    }

    private String showMyOfferedLoans(CustomerID customerID) {
        Customer customer = this.getCustomer(customerID);
        String result = "";

        for (Account account : customer.getAllAccounts()) {
            for(Loan loan: loans){
                if(loan.getAccountFrom().getAccountNumber().equals(account.getAccountNumber())){
                  result = result + "Loan Number: "+ loan.getNumber() +", Account Number: "+ loan.getAccountFrom().getAccountNumber() +", Amount: " + loan.getAmount() + ", Interest Rate: " + loan.getInterest() + "%"+ (loan.getAccountTo() == null ? "" : ", Taken by: " + loan.getAccountTo().getAccountNumber()) + "\n";
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
        account.setFrozenAmount(amount);
        loans.add(new Loan(amount, getAccount(accountNumber), term, interest, this.lastLoanNumber));
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

    private String getHelp(){
        String helpMsg = "\nPossible commands\n" +
                "Commands must be followed by user input values between <> and separated by a space\n\n" +
                getHelpShowMyAccounts() +
                getHelpNewAccount() +
                getHelpMove() +
                "PAY <Amount> <Debit account> <Credit account>\t\tPays funds from one account to another account, which may be held by another customer\n" +
                "OFFERLOAN <Amount> <FromAccount> <Terms> <intrest>\tCreates a loan for the specified period, under the defined conditions\n" +
                "SHOWMYOFFEREDLOANS\t\t\t\t\t\t\t\t\tShows all offered loans of the current customer\n" +
                "SHOWOPENLOANS\t\t\t\t\t\t\t\t\t\tShows all open loans with the conditions of the loan.\n" +
                "ACCEPTLOAN <Loan Number> <Account>\t\t\t\t\tThe open loan is accepted and the amount is credited to the given account.\n" +
                "PAYBACKLOAN <Loan Number>\t\t\t\t\t\t\tThe loan is repaid with interest\n" +
                "HELP\t\t\t\t\t\t\t\t\t\t\t\tShows this menu\n\n";
        return helpMsg;
    }

    private String getHelpShowMyAccounts() {
        return "SHOWMYACCOUNTS\t\t\t\t\t\t\t\t\t\tShows all of the current customer's account details\n";
    }

    private String getHelpNewAccount() {
        return "NEWACCOUNT <New account name>\t\t\t\t\t\tCreates a new account for the current customer with the specified name\n";
    }

    private String getHelpMove() {
        return "MOVE <Amount> <Debit account> <Credit account>\t\tMoves the amount specified between two of a customer's accounts\n";
    }
}
