package receiver;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.*;

/**
 * A GUI receiving and displaying telemetry data received via xBee radio on a
 * serial port.
 *  
 * @author Cody Brewer
 *
 */
public class Gui {
	
	/**
	 * Entry point
	 * @param args not used
	 */
	public static void main(String[] args){
		JTextArea log = new JTextArea(48, 38);
		DefaultCaret caret = (DefaultCaret)log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(log);
		
		JFrame guiFrame = new JFrame("Telemetry Receiver / Data Display");
		guiFrame.getContentPane().setLayout(new BorderLayout());
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container pane = guiFrame.getContentPane();
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();
		
		ChartPanel charts = new ChartPanel();
		DataManager dataManager = new DataManager(log, charts);
		ConnectionButtonPanel connections = new ConnectionButtonPanel(log, dataManager);
		dataManager.setConnectionPanel(connections);
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 5, 5, 0);
		panel1.add(charts, c);
		
		c.gridx = 1;
		c.gridy = 0;
		panel1.add(scroll, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		panel1.add(connections, c);
		
		JScrollPane scroller = new JScrollPane(panel1);
		pane.add(scroller, java.awt.BorderLayout.CENTER);
		PortManager portManager = new PortManager(log, connections, dataManager);
		connections.setManager(portManager);
		
		guiFrame.validate();
		guiFrame.setSize(800, 600);
		guiFrame.setVisible(true);
	}

}
