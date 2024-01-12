import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class ServerThread {
    private HashMap<String, Socket> clients = new HashMap<>();
    private HashMap<String, Boolean> isConnected = new HashMap<>();
    private ServerSocket serverSocket;

    public void run() throws IOException {
        serverSocket = new ServerSocket(8666);

        while (true) {
            System.out.println("Waiting for connections..");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connect:" + clientSocket.getInetAddress());

            Thread clientThread = new Thread(() -> handleClient(clientSocket, clients));
            clientThread.start();
        }
    }

    private void handleClient(Socket client, HashMap<String, Socket> clients) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String message;
            while ((message = bufferedReader.readLine()) != null) {
                System.out.println(client.getInetAddress() + " wrote a message.");
                if (message.contains("|")) {
                    Event event = new Event(message);
                    System.out.println(client.getInetAddress() + " used " + event.getEVENT() + " token with " + event.getPAYLOAD() + " payload.");

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

    private void broadcastMessage(Socket senderSocket, String payload) throws IOException {
        String senderUsername = getUsernameBySocket(senderSocket);
        System.out.println(senderUsername + " wrote " + payload);
        for (Socket client : clients.values()) {
            try {
                PrintWriter clientPrintWriter = new PrintWriter(client.getOutputStream());
                clientPrintWriter.println("new_message|" + senderUsername + "|" +payload+"|"+LocalTime.now());
                clientPrintWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public void connected(Socket clientSocket, String username) {
        clients.put(username, clientSocket);
        isConnected.put(username, true);

        for (Socket client : clients.values()) {
            try {
                PrintWriter clientPrintWriter = new PrintWriter(client.getOutputStream());
                clientPrintWriter.println("new_user|"+username+"|"+LocalTime.now());
                System.out.println(username + " connected to the chat.");
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