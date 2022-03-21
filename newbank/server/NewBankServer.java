package newbank.server;

import newbank.client.ExampleClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NewBankServer extends Thread {

    private ServerSocket server;

    public NewBankServer(int port, boolean help) throws IOException {
        server = new ServerSocket(port);
        if(help) {
            System.out.println(NewBank.getHelp());
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length >= 2) {
            if(args[1].equals("--help") || args[1].equals("-h")) {
                new NewBankServer(Integer.parseInt(args[0]), true).start();
            } else {
                new NewBankServer(Integer.parseInt(args[0]), false).start();
            }
        } else if(args.length >= 1) {
            if(args[0].equals("--help") || args[0].equals("-h")) {
                new NewBankServer(14002, true).start();
            } else {
                new NewBankServer(Integer.parseInt(args[0]), false).start();
            }
        } else {
            // starts a new NewBankServer thread on a specified port number
            new NewBankServer(14002, false).start();
        }
    }

    public void run() {
        // starts up a new client handler thread to receive incoming connections and process requests
        System.out.println("New Bank Server listening on " + server.getLocalPort());
        try {
            while (true) {
                Socket s = server.accept();
                NewBankClientHandler clientHandler = new NewBankClientHandler(s);
                clientHandler.start();
            }
        } catch (IOException e) {
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
