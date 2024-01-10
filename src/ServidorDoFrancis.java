import java.net.Socket;
import java.util.Scanner;

public class ServidorDoFrancis {

    int option;
    String text;
    String username;
    Scanner sc = new Scanner(System.in);
    private static final int PORT = 8666;
    private static final String LOCALHOST = "192.168.176.98";
    Socket userSocket;
    public void start(){
        try {
            do {
                System.out.println("1- Login\n2- Sign up\n0- Exit");
                switch (option = sc.nextInt()) {
                    case 1:
                            userSocket = new Socket(LOCALHOST, PORT);
                            break;
                    case 2:
                        userSocket = new Socket(LOCALHOST, PORT);
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("What?");
                }
                userSocket.close();
            } while (option != 0);
        } catch (Exception e) {
            System.out.println(e.getMessage() + ".\nServer unavailable!");
        }
    }
}