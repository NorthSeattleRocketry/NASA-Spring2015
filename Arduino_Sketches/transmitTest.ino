// Test sketch for sending data over xBee radio.
// Sends 'Hello World' to serial output about every
// 2 seconds. An LED connected to pin 3 will flash while
// transmitting.
void setup(){
  Serial.begin(9600);
  pinMode(3, OUTPUT);
}

void loop(){
  digitalWrite(3, HIGH);
  Serial.write("Hello World");
  delay(10); // keep LED on long enough to be visible
  digitalWrite(3, LOW);
  delay(2000);
}
