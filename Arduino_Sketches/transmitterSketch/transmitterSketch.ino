#include <MPL3115A2.h> //pressure/temperature sensor functions
#include <Wire.h> //for I2C comms with press/temp sensor
#include <SoftwareSerial.h> // for serial comms with GPS
#include <TinyGPS.h> //parses GPS data
#include <SD.h> // for sd card operations
#include <SPI.h> // for accelerometer

#define CSPIN 6
#define SCALE 0.000723421
#define SOFTRX 5
#define SOFTTX 4


/*
Test sketch for transmitting sensor data over xBee radio.
Transmits data from accelerometer, press/temp sensor, and GPS.
*/

MPL3115A2 ptaSensor; //pressure/temperature sensor
TinyGPS gps; //gps object
SoftwareSerial ss(SOFTRX, SOFTTX);
boolean newData, recording; // flags for new GPS data and SD card, respectively
double xAcc, yAcc, zAcc;

void setup(){
  pinMode(10, OUTPUT); //required for SD library
  pinMode(CSPIN, OUTPUT); // accelerometer chip select
  Serial.begin(9600); //open serial comms w/radio
  configureAltimeter();  //configure MPL315A2 temp/alt sensor
  ss.begin(4800); //software serial emulation for GPS
  pinMode(3, OUTPUT); //drives transmission indicator LED
  digitalWrite(3, HIGH);
  recording = false;
  configureAccel(); //configure LIS331 accelerometer
  
  // initialize SD card
  if(!SD.begin(8)){
    Serial.print("DEBUG,MSG:Could not initialize card&\n");
    recording = false;
  }
  else{
    Serial.print("DEBUG,MSG:Card Initialized&\n");
    recording = true;
  }
  
  
}

void loop(){
  newData = false;
  
  // get 3 rounds of temp/press/accel data for 
  // every round of GPS data. Due to slowness of GPS
  for(uint8_t i = 0; i < 4; i++){
    getAccel();
    transmitData();
    writeData();
  }
  
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
    Serial.print("&\n");

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
      Serial.print("DEBUG,MSG:Error opening SD file&\n");
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
  Serial.print("&\n");
  
  if (chars == 0){
    Serial.print("DEBUG,MSG:No characters received from GPS&\n");
  }
}
  
void transmitData(){
  // send press/temp/accel data
  Serial.print("DATA,TIME:");
  Serial.print(millis() / 1000.0);
  Serial.print(",TEMP:");
  Serial.print(ptaSensor.readTempF(), 2);
  Serial.print(",ALTI:");
  Serial.print(ptaSensor.readAltitudeFt(), 2);
  Serial.print(",XACC:");
  Serial.print(xAcc);
  // Z and Y coordinates switched to reflect actual 
  //   physical orientation of device                
  Serial.print(",ZACC:");
  Serial.print(yAcc);
  Serial.print(",YACC:");
  Serial.print(zAcc);
  Serial.print("&\n");
}


  
  // write to SD card if recording
void writeData(){
  if(recording){
    File dataFile = SD.open("packets.txt", FILE_WRITE);
    if(dataFile){
      dataFile.write("DATA,TIME:");
      dataFile.print(millis() / 1000.0);
      dataFile.write(",TEMP:");
      dataFile.print(ptaSensor.readTempF(), 2);
      dataFile.write(",ALTI:");
      dataFile.print(ptaSensor.readAltitudeFt(), 2);
      dataFile.write(",XACC:");
      dataFile.print(xAcc);
      dataFile.write(",YACC:");
      dataFile.print(yAcc);
      dataFile.write(",ZACC");
      dataFile.print(zAcc);
      dataFile.println("&");
      dataFile.close();
      Serial.print("DEBUG,MSG:Write to SD card&\n");
    }
    else{
      Serial.print("DEBUG,MSG:Error opening SD file&\n");
    }
  }
}

void configureAltimeter(){
  ptaSensor.begin();
  ptaSensor.setModeStandby();
  ptaSensor.enableEventFlags();
  ptaSensor.setModeAltimeter();
  ptaSensor.setOversampleRate(7);
  ptaSensor.setModeActive();
}

void configureAccel(){
  // configure SPI bus
  SPI.begin();
  SPI.setDataMode(SPI_MODE0); // set clock polarity and phase
  SPI.setBitOrder(MSBFIRST);
  SPI.setClockDivider(SPI_CLOCK_DIV16); // set clock timing
  
  // configure CTRL_REG1
  byte address = B00100000;
  //settings bits: 0-2: power mode; 3-4: data rate; 5-7: enable x y z axes
  byte settings = B00111111;
  digitalWrite(CSPIN, LOW); //address accelerometer
  delay(1);
  SPI.transfer(address); // get write access to address of CTRL_REG1
  SPI.transfer(settings);
  delay(1);
  digitalWrite(CSPIN, HIGH); // deselect accelerometer
  delay(100);

  // configure CTRL_REG4
  address = B00100011; // get write access to address of CTRL_REG4
  settings = B00110000; // select 24g scale
  digitalWrite(CSPIN,LOW);
  delay(1);
  SPI.transfer(address);
  SPI.transfer(settings);
  delay(1);
  digitalWrite(CSPIN, HIGH);

}

void getAccel(){
  byte lowXAddress = B11101000;
  byte empty = B00000000; //empty byte to advance register
  digitalWrite(CSPIN, LOW); //address accelerometer
  
  delay(1);
  SPI.transfer(lowXAddress); //get read access to OUT_X_L 
  byte xLow = SPI.transfer(empty); //send 0 to advance clock and get low x data
  byte xHigh = SPI.transfer(empty);
  byte yLow = SPI.transfer(empty);
  byte yHigh = SPI.transfer(empty);
  byte zLow = SPI.transfer(empty);
  byte zHigh = SPI.transfer(empty);
  delay(1);
  
  digitalWrite(CSPIN, HIGH);

  int xVal = (xLow | xHigh << 8);
  int yVal = (yLow | yHigh << 8);
  int zVal = (zLow | zHigh << 8);
  xAcc =  xVal * SCALE;
  yAcc =  yVal * SCALE;
  zAcc =  zVal * SCALE;
}


