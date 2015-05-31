package receiver;

//import jssc.SerialNativeInterface;
import jssc.SerialPortList;
import jssc.*;

public class SerialTest {
	
	public static void main(String[] args) {
		String[] portNames = SerialPortList.getPortNames();
		for(int i = 0; i < portNames.length; i++){
			System.out.println(portNames[i]);
		}
		
		if(portNames.length > 0){
			SerialPort serialPort = new SerialPort(portNames[0]);
			
			try {
				System.out.println("Port opened: " + serialPort.openPort());
				System.out.println("Params setted: " + 
						serialPort.setParams(9600, 8, 1, 0));
				System.out.println("\"Hello World!!!\" successfully written"
						+ " to port: " + serialPort.writeBytes("Hello World!!!"
								.getBytes()));
				System.out.println("Port closed: " + serialPort.closePort());
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
	
	}

}
