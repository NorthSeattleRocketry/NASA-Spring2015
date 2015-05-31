package receiver;

import java.io.UnsupportedEncodingException;
import javax.swing.JTextArea;
import jssc.*;

/**
 * Used to track which port is currently connected, and contains the
 * send/recieve logic for that port. Opens and closes ports.
 * 
 * @author Cody Brewer
 *
 */
public class PortManager implements SerialPortEventListener{
	
	JTextArea log;
	SerialPort currentPort;
	ConnectionButtonPanel connections;
	DataManager dataManager;

	/**
	 * Make a new port manager with the given components
	 * @param log
	 * @param pattern
	 * @param connections
	 */
	public PortManager(JTextArea log, ConnectionButtonPanel connections, DataManager dataman){
		
		this.log = log;
		currentPort = null;
		dataManager = dataman;
		
	}
	
	/**
	 * Set current port that is connected.
	 * 
	 * @param port
	 */
	public void connect(SerialPort port){
		currentPort = port;
		
		try {
			currentPort.addEventListener(this);
			currentPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} 
		catch (SerialPortException e) {
			e.getExceptionType();
		}
		

	}
	
	/**
	 * Null current port (not connected)
	 */
	public void disconnect(){
		currentPort = null;
	}
	
	/**
	 * Send given data to the current open port.
	 * 
	 * @param bytes the bytes to transmit
	 * TODO
	 */
	public void sendBytes(byte[] bytes){
		try{
			currentPort.writeBytes(bytes);
			log.append("Sending" + bytes + "\n");
		}
		catch(SerialPortException e){
			log.append(e.getExceptionType());
		}
	}
	
	/**
	 * Handles serial events.
	 * @param event the incoming SerialPortEvent
	 */
	public void serialEvent(SerialPortEvent event){
		if(event.isRXCHAR() && event.getEventValue() > 0){
			try{
				byte[] buffer = currentPort.readBytes();
				String packet = new String(buffer, "UTF-8");
				dataManager.assemblePacket(packet);
			}
			catch (SerialPortException e){
				log.append(e.getExceptionType());
			}
			catch(UnsupportedEncodingException ex){
				log.append(ex.getMessage());
			}
		}
	}
}
