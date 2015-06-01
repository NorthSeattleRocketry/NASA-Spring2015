# NASA-Spring2015

This repo contains design documents and code related to the NASA space grant project for the Spring 2015 quarter. 
The telemetry system was designed to capture data for Temperature, Altitude, GPS, and Acceleration during flight. 
Data was stored on an onboard SD card and transmitted via xBee radio in real time. Questions, comments, and critisism are all welcome. 

/Arduino_Sketches
--------------------
Arduino code for the transmitter and receiver stations. The transmitter is the payload mounted inside the rocket, powered by a 9v battery. It is fully functional without a receiver station, and data can be examined once the rocket has been retrieved and the SD card removed. For real-time data monitoring, connect a receiver station to a PC via USB cable. Data recieved can be displayed by either the Arduino serial monitor (accesed via Arduino IDE, text only) or the graphing program included in Data_Grapher.

/Data_Grapher
--------------------
Contains a runnable .jar for the graphing program (tested only on Windows 7), and the program's source code. The source code uses the Java Simple Serial Connector (jssc) library for serial port connections / operations, so you'll need to download it separately (jssc isn't hard to find, ask google) if you're going to rebuild a new jar. The program currently works, but there's a pretty significant amount of cleanup and documentation that needs to be done. 
Quick Usage: After connecting a receiver to the computer, run the graphing program. Click the "Port Scan" button to find the receiver station (should display as a COM port) and click connect. If valid data is being received it will start showing up on the graphs. Tick the "Show Packets" box to see incoming data from the receiver.

/Docs
--------------------
Contains a payload schematic and a parts list for both payload and receiver. The parts list contains links to product pages, which have links to manufacturer datasheets and design docs.
