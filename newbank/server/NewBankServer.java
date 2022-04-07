package newbank.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import newbank.util.SecurityUtilities;

public class NewBankServer extends Thread {

  private ServerSocket server;

  public NewBankServer(int port) throws IOException {
    server = new ServerSocket(port);
  }

  public static void main(String[] args) throws IOException {
    if (args.length >= 1) {
      new NewBankServer(Integer.parseInt(args[0])).start();
    } else {
      // starts a new NewBankServer thread on a specified port number
      new NewBankServer(14002).start();
    }
  }

  public void run() {
    // starts up a new client handler thread to receive incoming connections and process requests
    System.out.println("New Bank Server listening on " + server.getLocalPort());
    try {
      while (true) {
        Socket s = server.accept();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
          s.getOutputStream()
        );

        // send encryption public key
        KeyPair serverKeyPair = SecurityUtilities.generateRSAKeyPair();
        objectOutputStream.writeObject(serverKeyPair.getPublic());
        objectOutputStream.flush();
        // objectOutputStream.close();
        // receive public key from client
        ObjectInputStream objectInputStream = new ObjectInputStream(
            s.getInputStream()
        );
        PublicKey clientPublicKey = (PublicKey) objectInputStream.readObject();
        // objectInputStream.close();

        NewBankClientHandler clientHandler = new NewBankClientHandler(
          s,
          serverKeyPair,
          clientPublicKey
        );
        clientHandler.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        server.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }
}
