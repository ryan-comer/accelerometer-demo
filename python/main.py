from gui import GUI
import server as server

import threading
import time

collisions = {} # Dictionary to hold collision times
collisionThreshold = 5000    # Number of milliseconds between collisions

def startGUI():
    global g
    g = GUI()
    g.initGUI("Test Window")
    g.mainLoop()

# Callback for when the GET method is received
def getCallback():
    print("Hello World!")

# Callback for post requests
def postCallback(message):
    messageDict = dict(item.split("=") for item in message.split(";"))
    # New device connected
    if "connected" in messageDict:
        if messageDict["connected"] == "device1":
            g.setDeviceOneConnected(True)
        if messageDict["connected"] == "device2":
            g.setDeviceTwoConnected(True)

    # Collision message
    if "device1" in messageDict:
        collisions["device1"] = messageDict["device1"]
    if "device2" in messageDict:
        collisions["device2"] = messageDict["device2"]

    # Check if the collisions are in the time threshold
    if "device1" in collisions and "device2" in collisions:
        timeDifference = int(collisions["device1"]) - int(collisions["device2"])
        timeDifference = abs(timeDifference)
        if(timeDifference < collisionThreshold):
            g.setCollision(True)

# Callback for when the socket gets a message
def socketCallback(message):
    print(message)
    messageDict = dict(item.split("=") for item in message.split(";"))
    # New device connected
    if "connected" in messageDict:
        if messageDict["connected"] == "device1":
            g.setDeviceOneConnected(True)
        if messageDict["connected"] == "device2":
            g.setDeviceTwoConnected(True)

    # Collision message
    if "device1" in messageDict:
        collisions["device1"] = messageDict["device1"]
    if "device2" in messageDict:
        collisions["device2"] = messageDict["device2"]

    # Check if the collisions are in the time threshold
    if "device1" in collisions and "device2" in collisions:
        timeDifference = int(collisions["device1"]) - int(collisions["device2"])
        timeDifference = abs(timeDifference)
        if(timeDifference < collisionThreshold):
            print("Collision Detected")
            g.setCollision(True)

# Start the GUI thread
threading.Thread(target=startGUI).start()

# Open the HTTP server
server.registerSocketCallback(socketCallback)
server.run()
#server.registerGetCallback(getCallback)
#server.registerPostCallback(postCallback)
#server.run()
