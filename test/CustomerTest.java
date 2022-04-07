package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class CustomerTest extends test.MainTest {

  @Test
  public void createNewUser() {
    String result = test.createCustomer("Test", "P@ssw0rd123!");
    Assertions.assertEquals("Customer Test Created", result);
  }
  
  @Test
  public void createNewUserBadPassword() {
    String result = test.createCustomer("Test", "password");
    Assertions.assertEquals("Invalid Password", result);
  }
}
