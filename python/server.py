import socket
import threading

HOST = "192.168.1.107"
PORT = 8000

get_callbacks = []
post_callbacks = []
socket_callbacks = []

# Open the socket server for requests
def run():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen()

        while True:
            conn, addr = s.accept()
            with conn:
                print("Connected by", addr)

                data = conn.recv(1024)
                if not data:
                    break

                # Callbacks
                for callback in socket_callbacks:
                    callback(data.decode('utf-8'))

def registerSocketCallback(callback):
    socket_callbacks.append(callback)

# Register a callback for when a get call is made
def registerGetCallback(callback):
    get_callbacks.append(callback)

# Register a callback to be called when a POST is made
def registerPostCallback(callback):
    post_callbacks.append(callback)

if __name__ == "__main__":
    run()
