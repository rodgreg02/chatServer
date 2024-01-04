import java.net.*;
import java.io.*;
import java.util.List;

public class ServerThread extends Thread {
    private Socket clientSocket;
    private List<Socket> clients;
    private ServerSocket serverSocket;
    private final int PORT = 9999;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            while(!serverSocket.isClosed()){
                System.out.println("Waiting for connections!");
                clientSocket = serverSocket.accept();
                BufferedReader out = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                Event event = new Event(out.readLine());


                switch (event.getEVENT()) {
                    case "connect"-> connected(clientSocket);

                }
                connected(clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void connected(Socket clientSocket){
        clients.stream().forEach(e->new PrintWriter(e, true));



        clients.add(clientSocket);
    }
}

