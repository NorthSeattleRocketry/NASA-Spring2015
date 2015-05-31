package receiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveDialog extends JFrame {

	DataManager dataManager;
	JTextArea log;
	JButton confirmButton, cancelButton;
	JLabel saveText;
	JTextArea locationArea;
	Container pane;
	GridBagConstraints c;

	public SaveDialog(DataManager dataMan, JTextArea log){
		super("Confirm Save Data?");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setLayout(new GridBagLayout());

		dataManager = dataMan;
		this.log = log;
		confirmButton = new JButton("Confirm");
		cancelButton = new JButton("Cancel");
		saveText = new JLabel("Enter a location to save data:");
		locationArea = new JTextArea("C:\\users\\Cody\\Desktop\\packets.txt");

		pane = getContentPane();
		c = new GridBagConstraints();

		c.gridwidth = 3;
		pane.add(saveText, c);

		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		pane.add(locationArea, c);

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

		// clear data
		confirmButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){	
				if(dataManager.saveData()){
					setVisible(false);
					log.append("Data Saved\n");
				}
				else{
					log.append("Data NOT Saved\n");
				}

			}
		});
	}
}
