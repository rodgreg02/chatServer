import java.net.*;
import java.io.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServerThread extends Thread {
    private Socket clientSocket;
    private HashMap<String, Socket> clients = new HashMap<>();
    private ServerSocket serverSocket;
    private final int PORT = 9999;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            while(!serverSocket.isClosed()){
                System.out.println("Waiting for connections!");
                clientSocket = serverSocket.accept();
                System.out.println("Client " + clientSocket.getInetAddress() + " connected");

                Thread clientThread = new Thread(() -> handleClient(clientSocket, clients));
                clientThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Socket client, HashMap<String, Socket> clients) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String message = bufferedReader.readLine();

            if(message.contains("|")) {
                Event event = new Event(message);
                clients.put(event.getPAYLOAD(), client);

                switch (event.getEVENT()) {
                    case "connect" -> connected(clientSocket, event.getPAYLOAD());
                    case "send_message" -> broadcastMessage(clientSocket, event.getPAYLOAD());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastMessage(Socket clientSocket, String payload) throws IOException {
        String username = "null";

        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            String currentUsername = entry.getKey();
            Socket currentSocket = entry.getValue();

            if (clientSocket.equals(currentSocket)) {
                username = currentUsername;
                break;
            }
        }

        for (Socket s : clients.values()) {
            PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
            printWriter.println(LocalTime.now() + " " + username + ": " + payload);
            printWriter.flush();
        }
    }


    public void connected(Socket clientSocket, String username) {
        clients.values().forEach(client -> {
            try {
                PrintWriter clientPrintWriter = new PrintWriter(client.getOutputStream());
                clientPrintWriter.println(username + " entered in the chat.");
                clientPrintWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

