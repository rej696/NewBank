package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class NewBank implements NewBankQuery_I{

    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;
    private static final LoanManager loanManager = LoanManager.getLoanManager();

    private NewBank() {
        customers = new HashMap<>();
        addTestData();
    }

    public static NewBank getBank() {
        return bank;
    }

    public void clearLoans() {
        loanManager.clearLoans();
    }

    public void addCustomer(Customer customer, String customerID) {
        this.customers.put(customerID, customer);
    }

    private void addTestData() {
        Customer kim = new Customer();
        kim.addAccount(new Account(generateAccountNumber(), "Checking", 1050.0));
        customers.put("Kim", kim);

        Customer andy = new Customer("password");
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

    private ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customersArrayList = new ArrayList<Customer>();
        for(Customer c : customers.values()){
            customersArrayList.add(c);
        }
        return customersArrayList;
    }

    public Account getAccount(String accountNumber) {
        for (String key : this.customers.keySet()) {
            for (Account a : this.customers.get(key).getAllAccounts()) {
                if (accountNumber.equals(a.getAccountNumber())) {
                    return a;
                }
            }
        }
        return null;
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
            Customer customer = customers.get(userName);
            if (customer.correctPassword(password)) {
                return new CustomerID(userName);
            }
        }
        return null;
    }

    // commands from the NewBank customer are processed in this method
    public synchronized String processRequest(CustomerID customer, String request) {
        if (customers.containsKey(customer.getKey())) {
            String[] stringInputs = request.split(" ");
            String command = stringInputs[0];

            if (stringInputs.length == 2 && (stringInputs[1].equals("--help") || stringInputs[1].equals("-h"))) {
                switch (command) {
                    case "SHOWMYACCOUNTS":
                        return getHelpShowMyAccounts();
                    case "NEWACCOUNT": {
                        return getHelpNewAccount();
                    }
                    case "MOVE": {
                        return getHelpMove();
                    }
                    case "PAY": {
                        return getHelpPay();
                    }
                    case "OFFERLOAN": {
                        return getHelpOfferLoan();
                    }
                    case "SHOWMYOFFEREDLOANS": {
                        return getHelpShowMyOfferedLoans();
                    }
                    case "SHOWOPENLOANS": {
                        return getHelpShowOpenLoans();
                    }
                    case "ACCEPTLOAN": {
                        return getHelpAcceptLoan();
                    }
                    case "PAYBACKLOAN" : {
                        return getHelpPayBackLoan();
                    }
                    case "PARTPAYBACKLOAN" : {
                        return getHelpPartPayBackLoan();
                    }
                    case "MAKEMONTHLYPAYMENT" : {
                        return getHelpMakeMonthlyPayment();
                    }
                    case "SHOWTAKENLOANS" : {
                        return getHelpShowTakenLoans();
                    }
                    default:
                        return "FAIL";
                }
            } else {
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
                            return loanManager.offerLoan(Double.parseDouble(stringInputs[1]), stringInputs[2], Integer.parseInt(stringInputs[3]), Integer.parseInt(stringInputs[4]));
                        }
                    }
                    case "SHOWMYOFFEREDLOANS": {
                        return loanManager.showMyOfferedLoans(customer);
                    }
                    case "SHOWOPENLOANS": {
                        return loanManager.showOpenLoans(customer);
                    }
                    case "ACCEPTLOAN": {
                        if (stringInputs.length > 2) {
                            return loanManager.acceptLoan(customer, Integer.parseInt(stringInputs[1]), stringInputs[2]);
                        }
                    }
                    case "PAYBACKLOAN" : {
                        if (stringInputs.length > 1) {
                            return loanManager.paybackLoan(customer, Integer.parseInt(stringInputs[1]));
                        }
                    }
                    case "PARTPAYBACKLOAN" : {
                        if (stringInputs.length > 2) {
                            return loanManager.paybackLoanPartial(customer, Integer.parseInt(stringInputs[1]), Double.parseDouble(stringInputs[2]));
                        }                        
                    }
                    case "MAKEMONTHLYPAYMENT" : {
                        if (stringInputs.length > 1) {
                            return loanManager.payMonthlyPayment(customer, Integer.parseInt(stringInputs[1]));
                        }                        
                    }
                    case "SHOWTAKENLOANS" : {
                        return loanManager.showTakenLoans(customer);
                    }
                    case "HELP": {
                        return getHelp();
                    }
                    default:
                        return "FAIL";
                }
            }

        }
        return "FAIL";
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

    private boolean isValidPassword(String password) {
        boolean length = (password.length() > 7);
        boolean charTypes = Pattern.matches("[a-z&&A-Z&&0-9&&$+,:;=?@#|'<>.-^*()%!]",password);
        return length && charTypes;
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
                getHelpPay() +
                getHelpOfferLoan() +
                getHelpShowMyOfferedLoans() +
                getHelpShowOpenLoans() +
                getHelpAcceptLoan() +
                getHelpPayBackLoan() +
                getHelpPartPayBackLoan() +
                getHelpShowTakenLoans() +
                getHelpMakeMonthlyPayment() +
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

    private String getHelpPay() {
        return "PAY <Amount> <Debit account> <Credit account>\t\tPays funds from one account to another account, which may be held by another customer\n";
    }

    private String getHelpOfferLoan() {
        return "OFFERLOAN <Amount> <FromAccount> <Terms> <intrest>\tCreates a loan for the specified period, under the defined conditions\n";
    }

    private String getHelpShowMyOfferedLoans() {
        return "SHOWMYOFFEREDLOANS\t\t\t\t\t\t\t\t\tShows all offered loans of the current customer\n";
    }

    private String getHelpShowOpenLoans() {
        return "SHOWOPENLOANS\t\t\t\t\t\t\t\t\t\tShows all open loans with the conditions of the loan.\n";
    }

    private String getHelpAcceptLoan() {
        return "ACCEPTLOAN <Loan Number> <Account>\t\t\t\t\tThe open loan is accepted and the amount is credited to the given account.\n";
    }

    private String getHelpPayBackLoan() {
        return "PAYBACKLOAN <Loan Number>\t\t\t\t\t\t\tThe loan is repaid with interest\n";
    }

    private String getHelpPartPayBackLoan() {
        return "PARTPAYBACKLOAN <Loan Number> <Amount>\t\t\t\t\t\t\tThe amount is paid off the loan balance\n";
    }

    private String getHelpShowTakenLoans() {
        return "SHOWTAKENLOANS\t\t\t\t\t\t\t\t\t\tShows all taken loans of the current customer\n";
    }

    private String getHelpMakeMonthlyPayment() {
        return "MAKEMONTHLYPAYMENT <Loan Number>\t\t\t\t\t\t\tMakes the value of monthly payment against the loan number required to pay off the loan evenly over the loan period\n";
    }
}
