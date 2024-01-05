import java.net.*;
import java.io.*;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private final int PORT = 9999;
    private Map<String, Socket> clients = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
                Thread clientThread = new Thread(this::handleClient);
                clientThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void handleClient() {
        try {
                Socket client = serverSocket.accept();
                System.out.println("Client " + client.getInetAddress() + " connected");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String message = bufferedReader.readLine();

                if (message != null && message.contains("|")) {
                    Event event = new Event(message);
                    clients.put(event.getPAYLOAD(), client);

                    switch (event.getEVENT()) {
                        case "connect":
                            connected(event.getPAYLOAD());
                            break;
                        case "send_message":
                            broadcastMessage(client, event.getPAYLOAD());
                            break;
                    }
                }
            } catch(IOException e){
                throw new RuntimeException(e);
            }
    }

    private void broadcastMessage(Socket clientSocket, String payload) {
        String username = "null";

        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            String currentUsername = entry.getKey();
            Socket currentSocket = entry.getValue();

            if (currentSocket.equals(clientSocket)) {
                username = currentUsername;
                break;
            }
        }

        for (Socket s : clients.values()) {
            if (!clientSocket.equals(s)) {
                try {
                    PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
                    printWriter.println(username + "|" + LocalTime.now());
                    printWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void connected(String username) {
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
