package receiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadDialog extends JFrame {
	
	DataManager dataManager;
	JTextArea log;
	JButton confirmButton, cancelButton;
	JTextArea fileLocation;
	JLabel loadText;
	Container pane;
	GridBagConstraints c;
	
	public LoadDialog(DataManager dataman, JTextArea log){
		super("Confirm Clear Data?");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		dataManager = dataman;
		this.log = log;
		confirmButton = new JButton("Confirm");
		cancelButton = new JButton("Cancel");
		loadText = new JLabel("Enter location of file to load:");
		fileLocation = new JTextArea("C:\\Users\\Cody\\Desktop\\packets.txt");
		pane = getContentPane();
		
		c = new GridBagConstraints();
		pane.setLayout(new GridBagLayout());
		
		c.gridwidth = 3;
		pane.add(loadText, c);
		
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		pane.add(fileLocation, c);
		
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

		// clear current data and then load data from file
		confirmButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				log.append("Loading data from " + fileLocation.getText()
						+ "...\n");
	
				if(dataManager.loadData()){
					log.append("Data loaded\n");
				}
				else{
					log.append("Error loading data\n");
				}
			}
		});
	}
}
