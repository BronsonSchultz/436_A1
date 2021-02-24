import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    int port;

    HashMap<String, String> chatrooms;


    public Server(int port){
        this.port = port;
    }

    protected void listenForClients(ServerSocket serverSocket){
        System.out.println("Server is listening on port: " + serverSocket.getLocalPort());

        while (true){
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New Client connected on " + socket.getRemoteSocketAddress());
                ClientThread client = new ClientThread(this, socket);
                client.start();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

     protected void start(){
         ServerSocket serverSocket;
         try {
             serverSocket = new ServerSocket(port);
             listenForClients(serverSocket);
         } catch (IOException e){

         }
     }

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.start();
    }
}
