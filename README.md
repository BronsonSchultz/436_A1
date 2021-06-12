# Chatroom_Java_Python
This is a chatroom application thatruns in the linux terminal using the client and server architecture.  
The server is written in Java using threads to manage each connected client.  

The client is written in python.  

## How to run this project

This project requires a JDK to be installed on the server side, and python3 on the client side.  

### Server
To start up the server, open a terminal window and navigate to the repository's directory.  

Then,   
```
cd 436_server/out/production/436_server/
java Server
```  
this will start up the server on localhost:8000

### Client
open a new terminal tab and again navigate to the repository's directory.  
this time go to   
```
436_client/
```
then run `Client.py localhost 8000` to start up the client
