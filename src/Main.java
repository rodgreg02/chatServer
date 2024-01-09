import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        ServerThread serverThread = new ServerThread();
        serverThread.run();
    }
}