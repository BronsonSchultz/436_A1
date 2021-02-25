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
    String currentRoom;

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

    protected void executeCommand(String command){
        if(isValidCommand(command)){
            switch (command){
                case "/help": {
                    sendMsgToClient("""
                            /help --\s
                            /help - this list\s
                            /list - list all available chatrooms\s
                            /create <name> - create a new chatroom with the given name\s
                            /join <name> - join the chatroom of the given name\s
                            /leave <name> - leave a joined chatroom of the given name\s
                            /exit - exit the chatroom program completely""");
                }
                case "/list": {
                    //TODO

                }
                case "/create": {
                    sendMsgToClient("Name of the new room?: ");
                    try {
                        createRoom(readFromClient.readLine());
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }

                case "/join": {
                    sendMsgToClient("which room?: ");
                    try {
                        currentRoom = readFromClient.readLine();
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }
                case "leave": {

                }
            }
        }
    }

    protected void createRoom(String name){
        server.getChatrooms().put(name,"");
    }

    protected void saveMsgToRoom(String roomName, String message){
        String roomHistory = server.getChatrooms().get(roomName);
        roomHistory = roomHistory + message + "\n";
        server.getChatrooms().put(roomName, roomHistory);
    }

    protected void joinChatroom(String name){

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
                msgFromClient = readFromClient.readLine();
                executeCommand(msgFromClient);


                //System.out.println(msgFromClient);
            } while (!msgFromClient.equals("/exit"));

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
