package newbank.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import newbank.util.SecurityUtilities;

public class ExampleClient extends Thread {

  private final Socket server;
  private final DataOutputStream bankServerOut;
  private final BufferedReader userInput;
  private final Thread bankServerResponseThread;
  private final KeyPair clientKeyPair = SecurityUtilities.generateRSAKeyPair();
  private PublicKey serverPublicKey;

  public ExampleClient(String ip, int port) throws IOException {
    server = new Socket(ip, port);
    userInput = new BufferedReader(new InputStreamReader(System.in));
    bankServerOut = new DataOutputStream(server.getOutputStream());

    bankServerResponseThread =
      new Thread() {
        private final DataInputStream bankServerIn = new DataInputStream(
          server.getInputStream()
        );

        private String readLine() throws IOException {
          String response = SecurityUtilities.read(
            bankServerIn,
            clientKeyPair.getPrivate()
          );
          if (response != null) {
            return response;
          } else {
            return "NewBank Server Closed";
          }
        }

        public void run() {
          try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
              server.getInputStream()
            );
            serverPublicKey = (PublicKey) objectInputStream.readObject();

            while (true) {
              System.out.println(readLine());
            }
          } catch (IOException e) {
            e.printStackTrace();
            return;
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }
        }
      };
    bankServerResponseThread.start();
  }

  public static void main(String[] args)
    throws IOException, InterruptedException {
    // SecurityUtilities.init();
    if (args.length >= 2) {
      new ExampleClient(args[0], Integer.parseInt(args[1])).start();
    } else {
      new ExampleClient("localhost", 14002).start();
    }
  }

  public void run() {
    while (true) {
      try {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
          server.getOutputStream()
        );
        objectOutputStream.writeObject(clientKeyPair.getPublic());
        objectOutputStream.flush();
        // objectOutputStream.close();
        while (true) {
          String command = userInput.readLine();
          SecurityUtilities.send(bankServerOut, command, serverPublicKey);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
