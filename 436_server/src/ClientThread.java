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
        currentRoom = "";
    }

    protected void sendMsgToClient(String message){
        writeToClient.println(message);
        writeToClient.flush();
    }

    protected Boolean isCommand(String command){
        return command.charAt(0) == '/';
    }

    protected Boolean isValidCommand(String command){
        List<String> validCommands = Arrays.asList("/help", "/list", "/create", "/join", "/leave", "/exit");


        return validCommands.contains(command);
    }

    protected void executeCommand(String command){
        if(isValidCommand(command)){
            switch (command) {
                case "/help" -> {
                    sendMsgToClient("""
                            /help --\s
                            /help - this list\s
                            /list - list all available chatrooms\s
                            /create <name> - create a new chatroom with the given name\s
                            /join <name> - join the chatroom of the given name\s
                            /leave <name> - leave a joined chatroom of the given name\s
                            /exit - exit the chatroom program completely""");
                }
                case "/list" -> {
                    sendMsgToClient(list());
                }
                case "/create" -> {
                    sendMsgToClient("Name of the new room?: ");
                    try {
                        createRoom(readFromClient.readLine());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case "/join" -> {
                    sendMsgToClient("which room?: ");
                    try {
                        String room = readFromClient.readLine();
                        joinChatroom(room);
                        sendMsgToClient(getMsgsFromRoom(room));
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case "/leave" -> {
                    leave();
                }
                case "/exit" -> {
                    try {
                        sendMsgToClient("Goodbye!");
                        System.out.println(socket.getRemoteSocketAddress() + " left ");
                        socket.close();

                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } else {
            sendMsgToClient("That is not a valid command! type /help");
        }
    }

    protected synchronized void createRoom(String name) {
        if (server.getChatrooms().containsKey(name)) {
            sendMsgToClient("The chatroom already exists!");
        } else {
            server.getChatrooms().put(name, "");
            sendMsgToClient("created new room: " + name);
        }
    }

    protected synchronized void saveMsgToRoom(String roomName, String message){
        String roomHistory = server.getChatrooms().get(roomName);
        roomHistory = roomHistory + message + "\n";
        server.getChatrooms().put(roomName, roomHistory);
    }

    protected String getMsgsFromRoom(String roomName){
        if (server.getChatrooms().containsKey(roomName)){
            return server.getChatrooms().get(roomName);
        } else {
            sendMsgToClient("that chatroom doesn't exist!");
            return "";
        }
    }
    protected String list(){
        return server.getChatrooms().keySet().toString();
    }

    protected void joinChatroom(String name){
        if (server.getChatrooms().containsKey(name)) {
            currentRoom = name;
            sendMsgToClient("Joined room: "+ name);
        } else {
            sendMsgToClient("That chatroom doesn't exist!");
        }
    }

    protected void leave(){
        sendMsgToClient("Left room: " + currentRoom);

        currentRoom = "";
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

    protected String getMessagefromUser(){
        List<String> whitespace = Arrays.asList("", "\n", " ");
        String message = "";
        do {
            try {
                message = readFromClient.readLine();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        } while (whitespace.contains(message));
        return message;
    }

    @Override
    public void run(){
        try{
            writeToClient = new PrintWriter(socket.getOutputStream());
            readFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            loginUser();
            String msgFromClient;
            List<String> whitespace = Arrays.asList("", "\n", " ");
            do {
                msgFromClient = readFromClient.readLine();

                if(whitespace.contains(msgFromClient)){
                    continue;
                }


                if (isCommand(msgFromClient)) {
                    executeCommand(msgFromClient);
                } else {
                    if(currentRoom.equals("")){
                        sendMsgToClient("You are not currently in a room!");
                        continue;
                    }
                    sendMsgToClient("");
                    saveMsgToRoom(currentRoom, msgFromClient);
                    sendMsgToClient(getMsgsFromRoom(currentRoom));
                }
            } while (!msgFromClient.equals("/exit"));

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
