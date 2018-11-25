import tkinter as tk
from threading import Timer

class GUI:
    # Initialize the GUI to show the starting text
    def initGUI(self, windowTitle):
        # Create the components
        self.root = tk.Tk()
        self.root.title(windowTitle)

        self.deviceOneText = "Device One: Disconnected"
        self.deviceTwoText = "Device Two: Disconnected"
        self.collisionText = ""

        self.deviceOneLabel = tk.Label(self.root, text=self.deviceOneText)
        self.deviceTwoLabel = tk.Label(self.root, text=self.deviceTwoText)
        self.collisionLabel = tk.Label(self.root, text=self.collisionText)

    # Periodically check for updates to the UI
    def updateText(self):
        def update():
            self.deviceOneLabel.config(text=self.deviceOneText)
            self.deviceTwoLabel.config(text=self.deviceTwoText)
            self.collisionLabel.config(text=self.collisionText)
            self.root.after(50, update)
        update()

    # Start the GUI - should be called in another thread
    def mainLoop(self):
        self.deviceOneLabel.pack()
        self.deviceTwoLabel.pack()
        self.collisionLabel.pack()

        self.updateText()   # Periodically check for text updates

        self.root.mainloop()

    def setDeviceOneConnected(self, connected):
        if connected:
            self.deviceOneText = "Device One: Connected"
        else:
            self.deviceOneText = "Device One: Disconnected"

    def setDeviceTwoConnected(self, connected):
        if connected:
            self.deviceTwoText = "Device Two: Connected"
        else:
            self.deviceTwoText = "Device Two: Disconnected"

    def setCollision(self, collision):
        def reset():
            self.collisionText = ""

        if collision:
            self.collisionText = "Collision Detected!"
            t = Timer(2.0, reset)
            t.start()
        else:
            self.collisionText = ""
