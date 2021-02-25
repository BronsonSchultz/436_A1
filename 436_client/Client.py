import socket
import sys


# create our connection to the server
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# ip address and port number are received through the command line arguments
host = str(sys.argv[1])
port = int(sys.argv[2])

# connect to the server through those arguments
print("connecting to", host, "on port:", port, "...")
server.connect((host, port))
print('connected')



# while connected..
while True:
    # get any messages from the server
    data = server.recv(1024).decode()
    if not data:
        continue

    print(data)

    # send message to the server
    message = sys.stdin.readline()
    server.send(message.encode('utf8'))



