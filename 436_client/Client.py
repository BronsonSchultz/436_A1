import socket
import sys


server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
host = str(sys.argv[1])
port = int(sys.argv[2])

print("connecting to", host, "on port:", port, "...")
server.connect((host, port))
print('connected')


while True:

    data = server.recv(1024).decode()
    print(data)

    message = sys.stdin.readline().encode('utf8')
    server.send(message)



