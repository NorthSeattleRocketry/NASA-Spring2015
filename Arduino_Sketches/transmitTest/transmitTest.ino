#include "MPL3115A2.h"
#include <Wire.h>
#include <SoftwareSerial.h>
#include <TinyGPS.h>

/*
Test sketch for transmitting sensor data over xBee radio.
Transmits data from accelerometer and Temp/Press sensor about twice
every second.
*/
int xAcc;
int yAcc;
int zAcc;
float pressure;
float temperature; 
float alti;
MPL3115A2 ptaSensor; 
TinyGPS gps;
SoftwareSerial ss(4,5); //RX 4, TX 5

void setup(){
  Wire.begin();
  Serial.begin(9600); //open serial comms w/radio
  ss.begin(4800); //software serial emulation for GPS
  ptaSensor.begin(); // start pressure/temp monitoring
  ptaSensor.setModeAltimeter(); // set p/t sensor to altitude mode
  ptaSensor.setOversampleRate(7);
  ptaSensor.enableEventFlags();
  pinMode(3, OUTPUT); //drives transmission indicator LED
}

void loop(){

  xAcc = analogRead(0);
  yAcc = analogRead(1);
  zAcc = analogRead(2);
 
  alti = ptaSensor.readAltitude();
  pressure = ptaSensor.readPressure();
  temperature = ptaSensor.readTempF();
  
  digitalWrite(3, HIGH); // turn on LED while transmitting
  
  Serial.print("Pressure(Pa):");
  Serial.print(pressure, 2);
  Serial.print("Temp(f):");
  Serial.print(temperature, 2);
  Serial.print("Altitude(ft):");
  Serial.print(alti, 2);
  Serial.write("  Acceleration  X:");
  Serial.print(xAcc);
  Serial.write(" Y:");
  Serial.print(yAcc);
  Serial.write(" Z:");
  Serial.print(zAcc);
  Serial.write("&"); // packet termination character
  delay(10); // keep LED on long enough to be visible
  digitalWrite(3, LOW); //turn off LED
  delay(500);
}
