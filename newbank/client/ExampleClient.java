package newbank.client;

import newbank.server.NewBank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ExampleClient extends Thread {

    private final Socket server;
    private final PrintWriter bankServerOut;
    private final BufferedReader userInput;
    private final Thread bankServerResponceThread;

    public ExampleClient(String ip, int port, boolean help) throws IOException {
        server = new Socket(ip, port);
        userInput = new BufferedReader(new InputStreamReader(System.in));
        bankServerOut = new PrintWriter(server.getOutputStream(), true);
        if(help) {
            System.out.println(NewBank.getHelp());
        }

        bankServerResponceThread = new Thread() {
            private final BufferedReader bankServerIn = new BufferedReader(new InputStreamReader(server.getInputStream()));

            public void run() {
                try {
                    while (true) {
                        String responce = bankServerIn.readLine();
                        System.out.println(responce);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        bankServerResponceThread.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length >= 2) {
            if(args[2].equals("--help") || args[2].equals("-h")) {
                new ExampleClient(args[0], Integer.parseInt(args[1]), true).start();
            } else {
                new ExampleClient(args[0], Integer.parseInt(args[1]), false).start();
            }
        } else if (args.length >= 1) {
            if(args[0].equals("--help") || args[0].equals("-h")) {
                new ExampleClient("localhost", 14002, true).start();
            } else {
                new ExampleClient("localhost", 14002, false).start();
            }
        } else {
            new ExampleClient("localhost", 14002, false).start();
        }
    }

    public void run() {
        while (true) {
            try {
                while (true) {
                    String command = userInput.readLine();
                    bankServerOut.println(command);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
