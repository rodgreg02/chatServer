import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashMap;
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
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for connections!");
                clientSocket = serverSocket.accept();
                System.out.println("Client " + clientSocket.getInetAddress() + " connected");

                Thread clientThread = new Thread(() -> handleClient(clientSocket, clients));
                clientThread.start();
            }
        } catch (IOException e) {
            if (serverSocket.isClosed()) {
                System.out.println("Server socket is closed. Exiting server thread.");
            } else {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket client, HashMap<String, Socket> clients) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String message;
            while ((message = bufferedReader.readLine()) != null) {
                if (message.contains("|")) {
                    Event event = new Event(message);

                    switch (event.getEVENT()) {
                        case "connect" -> connected(client, event.getPAYLOAD());
                        case "send_message" -> broadcastMessage(client, event.getPAYLOAD());
                    }
                }
            }
        } catch (IOException e) {
            String username = getUsernameBySocket(client);
            if (username != null) {
                clients.remove(username);
                System.out.println("Client " + client.getInetAddress() + " disconnected.");
            }
        }
    }

    private void broadcastMessage(Socket senderSocket, String payload) {
        String senderUsername = getUsernameBySocket(senderSocket);

        for (Socket receiverSocket : clients.values()) {
            if (!senderSocket.equals(receiverSocket)) {
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(receiverSocket.getOutputStream()));
                    writer.write(senderUsername + ": " + payload + " - (" + LocalTime.now() + ")\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void connected(Socket clientSocket, String username) {
        clients.put(username, clientSocket);

        for (Socket client : clients.values()) {
            try {
                PrintWriter clientPrintWriter = new PrintWriter(client.getOutputStream());
                clientPrintWriter.println(username + " entered in the chat.");
                clientPrintWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getUsernameBySocket(Socket clientSocket) {
        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            if (entry.getValue().equals(clientSocket)) {
                return entry.getKey();
            }
        }
        return null;
    }
}