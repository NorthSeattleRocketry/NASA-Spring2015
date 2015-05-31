package receiver;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import jssc.*;
import java.awt.Color;

/**
 * A panel containing the buttons associated with the serial connection.
 * 
 * @author Cody Brewer
 *
 */
@SuppressWarnings("serial")
public class ConnectionButtonPanel extends JPanel {
	PortManager portManager;
	HashMap<String, SerialPort> portMap;
	JButton connect, scan, disconnect, save, clear, load, placeholder;
	JPanel buttons;
	JComboBox<String> ports;
	JLabel title, connectionIndicator;
	JTextArea log;
	JCheckBox debug, showPackets; 
	DataManager dataManager;
	
	
	/**
	 * Makes a new panel.
	 * 
	 * @param log the text area used to report actions/updates to user
	 */
	public ConnectionButtonPanel(JTextArea log, DataManager dataMan){
		super(new BorderLayout());
		
		dataManager = dataMan;
		this.log = log;
		portMap = new HashMap<String, SerialPort>();
		connect = new JButton("Connect");
		scan = new JButton("Port Scan");
		disconnect = new JButton("Disconnect");
		save = new JButton("Save Data");
		clear = new JButton("Clear");
		load = new JButton("Load Data");
		placeholder = new JButton();
		placeholder.setEnabled(false);
		placeholder.setVisible(false);
		debug = new JCheckBox("Debug");
		showPackets = new JCheckBox("Show Packets");
		ports = new JComboBox<String>();
		connectionIndicator = new JLabel("Connection");
		connectionIndicator.setForeground(Color.red);
		connectionIndicator.setHorizontalAlignment(JLabel.CENTER);
		buttons = new JPanel(new GridLayout(2, 5, 5, 5));
		
		buttons.add(connect);
		buttons.add(scan);
		buttons.add(save);
		buttons.add(clear);
		buttons.add(debug);
		buttons.add(disconnect);
		buttons.add(ports);
		buttons.add(load);
		buttons.add(placeholder);
		buttons.add(showPackets);
		
		setListeners();
		
		add(buttons, BorderLayout.CENTER);
		add(connectionIndicator, BorderLayout.SOUTH);
		
		disableButtons();
		
	}
	
	/**
	 * Set the PortManager this panel will interact with.
	 * 
	 * @param man the PortManager to use
	 */
	public void setManager(PortManager man){
		portManager = man;
	}
	
	/**
	 * Set action listeners for the buttons. this is where the actual serial
	 * comms will be implemented.
	 * 
	 */
	private void setListeners(){
		
		//open the port that is selected in the combobox
		connect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					portMap.get(ports.getSelectedItem()).openPort();
					portMap.get(ports.getSelectedItem()).setParams(
							SerialPort.BAUDRATE_9600,
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					portManager.connect(portMap.get(ports.getSelectedItem()));
					disconnect.setEnabled(true);
					save.setEnabled(false);
					load.setEnabled(false);
					log.append("Connected to port " +
							portManager.currentPort.getPortName() + "\n");
				}
				catch(SerialPortException ex){
					log.append(ex.getExceptionType() + "\n");
				}
			}
		});
		
		// disconnect from current port, disable appropriate buttons 
		disconnect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(portManager.currentPort != null){
					try{
						portMap.get(ports.getSelectedItem()).closePort();
						log.append("Port disconnected\n");
						portManager.disconnect();
					}
					catch(SerialPortException ex){
						log.append(ex.getExceptionType() + "\n");
					}
				}
				disableButtons();
				save.setEnabled(true);
				load.setEnabled(true);
			}
		});
		
		// clear ports selection box and disable all other buttons
		// if a port is found, enable 'connect'
		scan.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				disconnect.doClick();
				ports.removeAllItems();
				disableButtons();
				String[] portNames = SerialPortList.getPortNames();
				
				if(portNames.length > 0){
					log.append("Found " + portNames.length + " port(s)\n");
					connect.setEnabled(true);
					ports.setEnabled(true);

					for(int i = 0; i < portNames.length; i++){
						portMap.put(portNames[i], new SerialPort(portNames[i]));
						ports.addItem(portNames[i]);
					}
				}
				else{
					log.append("No available ports found\n");
				}
			}
		});
		
		// open a dialog box and enable user to save data stored in
		// packets array
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dataManager.showSave();
			}
		});
		
		// warn user before clearing charts, data, and log
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dataManager.showClear();
			}
		});
		
		// load data from file and display on graphs
		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dataManager.showLoad();
			}
		});

		
	}
	
	private void disableButtons(){
		connect.setEnabled(false);
		disconnect.setEnabled(false);
		ports.setEnabled(false);
	}

}
