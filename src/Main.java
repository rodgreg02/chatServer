public class Main {
    public static void main(String[] args) throws InterruptedException {
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }
}