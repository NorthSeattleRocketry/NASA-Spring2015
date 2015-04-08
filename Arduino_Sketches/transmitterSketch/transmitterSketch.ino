#include <MPL3115A2.h> //pressure/temperature sensor functions
#include <Wire.h> //for I2C comms with press/temp sensor
#include <SoftwareSerial.h> // for serial comms with GPS
#include <TinyGPS.h> //parses GPS data
#include <SD.h> // for sd card operations
#include <SPI.h> // for accelerometer

/*
Test sketch for transmitting sensor data over xBee radio.
Transmits data from accelerometer, press/temp sensor, and GPS.
*/

MPL3115A2 ptaSensor; //pressure/temperature sensor
TinyGPS gps; //gps object
SoftwareSerial ss(5,4); //RX 5, TX 4
boolean newData, recording; // flags for new GPS data and SD card, respectively

void setup(){
  pinMode(10, OUTPUT); //required for SD library
  Wire.begin(); //join I2C bus
  Serial.begin(9600); //open serial comms w/radio
  ss.begin(4800); //software serial emulation for GPS
  ptaSensor.begin(); // start pressure/temp monitoring
  ptaSensor.setModeAltimeter(); // set p/t sensor to altitude mode
  ptaSensor.setOversampleRate(7);
  ptaSensor.enableEventFlags();
  pinMode(3, OUTPUT); //drives transmission indicator LED
  digitalWrite(3, HIGH);
  pinMode(6, OUTPUT); //CS pin for accelerometer
  recording = false;
  configureAccel();
  
  // initialize SD card
  if(!SD.begin(8)){
    Serial.println("DEBUG,MSG:Could not initialize card&");
    recording = false;
  }
  else{
    Serial.println("DEBUG,MSG:Card Initialized&");
    recording = true;
  }
  
  
}

void loop(){
  newData = false;
  
  //get X acceleration from new accelerometer
  Serial.print("New X ACCELRERATION:");
  Serial.println(getXAccel());
  
  // get GPS data
  for (unsigned long start = millis(); millis() - start < 150;)
  {
    while(ss.available()){
      int c = ss.read();
      if (gps.encode(c)){
        newData = true;
      }
    }
  }
  
  digitalWrite(3, HIGH); // turn on LED while transmitting
  transmitAndWriteGPSData();
  transmitGPSDebug();
  transmitData();
  writeData();
  digitalWrite(3, LOW); //turn off LED
}

  // send GPS data
  void transmitAndWriteGPSData(){
  if (newData){
    float flat, flon;
    unsigned long age;
    gps.f_get_position(&flat, &flon, &age);
        
    Serial.print("GPS,");
    Serial.print("TIME:");
    Serial.print(millis() / 1000.0);
    // Serial.print(TinyGPS::_time);
    Serial.print(",LAT:");
    Serial.print(flat == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : flat, 6);
    Serial.print(",LON:");
    Serial.print(flon == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : flon, 6);
    Serial.println("&");

    // write to SD card
    File dataFile = SD.open("packets.txt", FILE_WRITE);
      
    if(dataFile){
      dataFile.print("GPS,");
      dataFile.print("TIME:");
      dataFile.print(millis() / 1000.0);
      dataFile.print(",LAT:");
      dataFile.print(flat == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : flat, 6);
      dataFile.print(",LON:");
      dataFile.print(flon == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : flon, 6);
      dataFile.println("&");
      dataFile.close();
      }
      else{
        Serial.println("DEBUG,MSG:Error opening SD file&");
      }
    }
   }
  
  
  // send GPS statistics
  void transmitGPSDebug(){
  unsigned long chars;
  unsigned short sentences, failed;
  gps.stats(&chars, &sentences, &failed);
  Serial.print("DEBUG,TIME:");
  Serial.print(millis() / 1000.0);
  Serial.print(",CHARS:");
  Serial.print(chars);
  Serial.print(",SENTENCES:"); 
  Serial.print(sentences);
  Serial.print(",ERR:");
  Serial.print(failed);
  Serial.println("&");
  if (chars == 0){
    Serial.println("DEBUG,MSG:No characters received from GPS&");
  }
  }
  
  void transmitData(){
  // send press/temp/accel data
  Serial.print("DATA,TIME:");
  Serial.print(millis() / 1000.0);
  Serial.print(",TEMP:");
  Serial.print(ptaSensor.readTempF(), 2);
  Serial.print(",ALTI:");
  Serial.print(ptaSensor.readAltitude(), 2);
  Serial.print(",XACC:");
  Serial.print(analogRead(0));
  Serial.print(",YACC:");
  Serial.print(analogRead(1));
  Serial.print(",ZACC:");
  Serial.print(analogRead(2));
  Serial.print("&\n");
  }


  
  // write to SD card if recording
  void writeData(){
//  if(abs(temperature + alti + xAcc + yAcc + zAcc - lastPacketValue) > 5 && recording){
  if(recording){
    File dataFile = SD.open("packets.txt", FILE_WRITE);
    if(dataFile){
      dataFile.write("DATA,TIME:");
      dataFile.print(millis() / 1000.0);
      dataFile.write(",TEMP:");
      dataFile.print(ptaSensor.readTempF(), 2);
      dataFile.write(",ALTI:");
      dataFile.print(ptaSensor.readAltitude(), 2);
      dataFile.write(",XACC:");
      dataFile.print(analogRead(0));
      dataFile.write(",YACC:");
      dataFile.print(analogRead(1));
      dataFile.write(",ZACC");
      dataFile.print(analogRead(2));
      dataFile.println(",&");
      dataFile.close();
      Serial.println("SD write");

    }
    else{
      Serial.println("DEBUG,MSG:Error opening SD file&");
    }
  }
}
  /**
  else if(recording){
    Serial.println("Duplicate packet - not recorded to SD");
  }**/

void configureAccel(){
  SPI.begin();
  digitalWrite(6, LOW); //address accelerometer
  SPI.setDataMode(SPI_MODE3); // set clock polarity and phase
  SPI.setBitOrder(MSBFIRST);
  
  //write access,do not advance address automatically, CTRL_REG1
  SPI.transfer(32); // 00100000b
  //normal power, data rate, enable x y z axes 
  SPI.transfer(39); // 00100111b
  digitalWrite(6, HIGH); // deselect accelerometer
  SPI.end();
}

int getXAccel(){
  SPI.begin();
  digitalWrite(6, LOW); //address accelerometer
  SPI.setDataMode(SPI_MODE3); // set clock polarity and phase
  SPI.setBitOrder(MSBFIRST);
  
  SPI.transfer(168); //10101000b get read access to OUT_X_L 
  byte xLow = SPI.transfer(0); //send 0 to advance clock and get low x data
  
  digitalWrite(6, HIGH); //reset accel for new address
  delay(1);
  digitalWrite(6, LOW);
  SPI.transfer(169); //10101001b get read access to OUT_X_H
  byte xHigh = SPI.transfer(0); //send 0 to advance clock and get high x data
  digitalWrite(6, HIGH);
  
  int w = xHigh << 8 | xLow;
  return w;
}
  
  
  

