package receiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class ClearDialog extends JFrame {
	
	DataManager dataManager;
	LinkedList<String> packets;
	JTextArea log;
	JButton confirmButton, cancelButton;
	JCheckBox confirmBox;
	JLabel warningText;
	Container pane;
	GridBagConstraints c;
	
	public ClearDialog(DataManager dataMan, LinkedList<String> packetList, JTextArea log){
		super("Confirm Clear Data?");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		dataManager = dataMan;
		packets = packetList;
		this.log = log;
		confirmButton = new JButton("Confirm");
		cancelButton = new JButton("Cancel");
		confirmBox = new JCheckBox("I know what I'm doing");
		warningText = new JLabel("WARNING!\n All unsaved chart data and "
				+ "received packets will be lost.");
		
		pane = getContentPane();
		
		c = new GridBagConstraints();
		pane.setLayout(new GridBagLayout());
		
		c.gridwidth = 3;
		pane.add(warningText, c);
		
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		pane.add(confirmBox, c);
		
		c.gridx = 0;
		c.gridy = 2;
		pane.add(confirmButton, c);
		
		c.gridx = 2;
		pane.add(cancelButton, c);
		
		setListeners();
		validate();
		setSize(400, 200);
		setVisible(false);
	}
	
	private void setListeners(){
		
		// hide window and do nothing
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});
		
		// clear data and hide window
		confirmButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(confirmBox.isSelected()){
					dataManager.clearData();
					confirmBox.setSelected(false);
					setVisible(false);
				}
			}
		});
	}
}
