import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientThread extends Thread{
    Socket socket;
    PrintWriter writeToClient;
    BufferedReader readFromClient;
    Server server;
    String username;

    public ClientThread(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    protected void sendMsgToClient(String message){
        writeToClient.println(message);
        writeToClient.flush();
    }

    protected Boolean isValidCommand(String command){
        List<String> validCommands = Arrays.asList("/help", "/list", "/create", "/join");

        if (command.charAt(0) == '/') {
            if (validCommands.contains(command)){
                return true;
            }
            else {
                sendMsgToClient("Not a valid command, type '/help' to see the available commands!");
                return false;
            }
        } else {
            return false;
        }
    }

    protected void loginUser() {
        sendMsgToClient("Please enter your name: ");
        try {
            username = readFromClient.readLine();
            sendMsgToClient("Welcome " + username + '!');
        } catch (IOException e){
            sendMsgToClient(e.getMessage());
        }
    }

    @Override
    public void run(){
        try{
            writeToClient = new PrintWriter(socket.getOutputStream());
            readFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            loginUser();
            String msgFromClient;
           do {
               System.out.println(socket.isClosed());
                msgFromClient = readFromClient.readLine();
                System.out.println(msgFromClient);
            } while (!msgFromClient.equals("/exit"));

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
