package newbank.server;

public interface NewBankQuery_I {
  
  public Customer getCustomer(CustomerID customerID);

  public Account getAccount(String accountNumber);

}
