package newbank.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import newbank.util.SecurityUtilities;

public class NewBankClientHandler extends Thread {

  private NewBank bank;
  private DataInputStream in;
  private DataOutputStream out;

  private final KeyPair serverKeyPair;
  private final PublicKey clientPublicKey;

  public NewBankClientHandler(
    Socket s,
    KeyPair serverKeyPair,
    PublicKey clientPublicKey
  )
    throws IOException {
    this.serverKeyPair = serverKeyPair;
    this.clientPublicKey = clientPublicKey;
    bank = NewBank.getBank();
    in = new DataInputStream(s.getInputStream());
    out = new DataOutputStream(s.getOutputStream());
  }

  public void run() {
    // keep getting requests from the client and processing them
    try {
      while (true) {
        printLn("Log On (LOGON) or Create a User (CREATENEWUSER) or EXIT");
        switch (readLn()) {
          case "LOGON":
            logOn();
            break;
          case "CREATENEWUSER":
            createNewUser();
            break;
        }
      }
    } catch (EOFException e) {} catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  private void logOn() throws IOException {
    // ask for user name
    printLn("Enter Username");
    String userName = readLn();
    // ask for password
    printLn("Enter Password");
    String password = SecurityUtilities.getSecurePassword(readLn());
    //   String password = readLn();
    printLn("Checking Details...");
    // authenticate user and get customer ID token from bank for use in subsequent requests
    CustomerID customer = bank.checkLogInDetails(userName, password);
    // if the user is authenticated then get requests from the user and process them
    if (customer != null) {
      printLn("Log In Successful. What do you want to do?");
      while (true) {
        String request = readLn();
        System.out.println("Request from " + customer.getKey());

        if (request.equals("LOGOUT")) {
          break;
        }

        String response = bank.processRequest(customer, request);
        printLn(response);
        // Serialize Bank Object
        NewBank.save();
      }
    } else {
      printLn("Log In Failed");
    }
  }

  private void createNewUser() throws IOException {
    // ask for user name
    printLn("Enter Username");
    String userName = readLn();
    // ask for password
    printLn("Enter Password");
    String password = readLn();

    printLn(bank.createCustomer(userName, password));
  }

  private void printLn(String s) throws IOException {
    SecurityUtilities.send(out, s, clientPublicKey);
  }

  private String readLn() throws IOException {
    return SecurityUtilities.read(in, serverKeyPair.getPrivate());
  }
}
