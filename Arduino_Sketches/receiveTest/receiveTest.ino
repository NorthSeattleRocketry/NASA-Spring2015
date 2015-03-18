// Test sketch for receiving data over xBee radio.
// Any data received over serial in will be converted to 
// a string and shown on the serial monitor.
void setup(){
  Serial.begin(9600);
}

void loop(){
  while(Serial.available() > 0){
    Serial.println(Serial.readStringUntil('&'));
  }
}
