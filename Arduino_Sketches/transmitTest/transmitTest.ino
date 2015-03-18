#include "MPL3115A2.h" //pressure/temperature sensor functions
#include <Wire.h> //for I2C comms with press/temp sensor
#include <SoftwareSerial.h> // for serial comms with GPS
#include <TinyGPS.h> //parses GPS data

/*
Test sketch for transmitting sensor data over xBee radio.
Transmits data from accelerometer, press/temp sensor, and GPS.
*/
int xAcc, yAcc, zAcc;
float pressure, temperature, alti;
long lat, lon;
unsigned long chars;
unsigned short sentences, failed;
MPL3115A2 ptaSensor; 
TinyGPS gps;
SoftwareSerial ss(5,4); //RX 5, TX 4
boolean newData;

void setup(){
  Wire.begin(); //join I2C bus
  Serial.begin(9600); //open serial comms w/radio
  ss.begin(4800); //software serial emulation for GPS
  ptaSensor.begin(); // start pressure/temp monitoring
  ptaSensor.setModeAltimeter(); // set p/t sensor to altitude mode
  ptaSensor.setOversampleRate(7);
  ptaSensor.enableEventFlags();
  pinMode(3, OUTPUT); //drives transmission indicator LED
}

void loop(){
  newData = false;
  
  // get accelerometer data
  xAcc = analogRead(0);
  yAcc = analogRead(1);
  zAcc = analogRead(2);
 
  // get temp/pressure data
  alti = ptaSensor.readAltitude();
  pressure = ptaSensor.readPressure();
  temperature = ptaSensor.readTempF();
  
  // get GPS data
  for (unsigned long start = millis(); millis() - start < 1000;)
  {
  while(ss.available()){
    int c = ss.read();
    if (gps.encode(c)){
      newData = true;
    }
  }
  }
  
  digitalWrite(3, HIGH); // turn on LED while transmitting
  
  // send GPS data
  if (newData){
    float flat, flon, falt, fvel, fcrs;
    unsigned long age;
    gps.f_get_position(&flat, &flon, &age);
    falt = gps.f_altitude();
    fvel = gps.f_speed_mps();
    fcrs = gps.f_course();
    Serial.print("LAT=");
    Serial.print(flat == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : flat, 6);
    Serial.print(" LON=");
    Serial.print(flon == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : flon, 6);
  }
  
  // send GPS statistics
  gps.stats(&chars, &sentences, &failed);

  Serial.print(" CHARS=");
  Serial.print(chars);
  Serial.print(" SENTENCES=");
  Serial.print(sentences);
  Serial.print(" ERR=");
  Serial.println(failed);
  if (chars == 0){
    Serial.println("** No characters received from GPS: check wiring **");
  }
  
  
  // send press/temp/accel data
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
  Serial.println();
  delay(10); // keep LED on long enough to be visible
  digitalWrite(3, LOW); //turn off LED
  delay(500);
}
