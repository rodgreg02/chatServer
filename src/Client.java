import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    public static void main(String[] args) {
        Client client = new Client();
        Thread t1 = new Thread(client);
        t1.start();
    }

    private Socket clientSocket;
    private PrintWriter printWriter;
    private Scanner scanner;
    private boolean isConnected = false;

    @Override
    public void run() {
        try {
            clientSocket = new Socket("192.168.176.204", 8667);
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            scanner = new Scanner(System.in);

            Thread readingThread = new Thread(this::startReading);
            readingThread.start();

            while (true) {
                if(!isConnected) {
                    System.out.print("Username: ");
                    String firstTime = "connect|" + scanner.nextLine();
                    printWriter.println(firstTime);
                    printWriter.flush();
                    isConnected = true;
                }

                String message = scanner.nextLine();
                printWriter.println(message);
                printWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReading() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message;
            while ((message = bufferedReader.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
