package receiver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import javax.swing.JTextArea; 


public class DataManager {
	LinkedList<String> packets;
	JTextArea log;
	ChartPanel charts;
	float lastPacketTime;
	String currentPacket;
	ConnectionButtonPanel connectionPanel;
	ClearDialog clearDialog;
	SaveDialog saveDialog;
	LoadDialog loadDialog;
	LinkedList<Byte> byteBuffer;
	

	public DataManager(JTextArea log, ChartPanel chartPanel) {
		packets = new LinkedList<String>();
		this.log = log;
		charts = chartPanel;
		currentPacket = "";
		connectionPanel = null;
		clearDialog = new ClearDialog(this, packets, log);
		saveDialog = new SaveDialog(this, log);
		loadDialog = new LoadDialog(this, log);
		lastPacketTime = 0;
	}
	
	public void assemblePacket(String packetPiece){
		currentPacket += packetPiece;
		if(currentPacket.contains("&")){
			try{
				String[] splitPacket = currentPacket.split("&");
				if(splitPacket.length > 1){
					currentPacket = splitPacket[1];
				}
				else{
					currentPacket = "";
				}
				parse(splitPacket[0]);
			}
			catch(Exception e){
				if(connectionPanel.debug.isSelected()){
					log.append("Error parsing packet: " + e.getMessage());
				}
			}
		}
	}

	private void parse(String packet){
		packets.add(packet);
		packet = packet.replace("\n", "");
		String[] data = (packet.split(","));
		switch(data[0]) {
		case "DATA": if(connectionPanel.showPackets.isSelected()){
			log.append(packet + "\n");			
		}
		sortData(data);  
		break;

		case "GPS":if(connectionPanel.showPackets.isSelected()){
			log.append(packet + "\n");			
		}
		sortData(data);
		break;

		case "DEBUG": if(connectionPanel.debug.isSelected()){
			log.append(packet + "\n");
		}
		break;
		
		default: 
			if(connectionPanel.debug.isSelected()){
				log.append("Invalid Packet: Incorrect Header - :" + data[0] + ":\n");
				log.append("Invalid Packet Contents:" + packet + "\n");
			}
		}
		
	}
	
	public void showSave(){
		saveDialog.setVisible(true);
	}
	
	public void showClear(){
		clearDialog.setVisible(true);
	}
	
	public void showLoad(){
		loadDialog.setVisible(true);
	}
	
	public void setConnectionPanel(ConnectionButtonPanel connections){
		connectionPanel = connections;
	}
	

	public boolean loadData(){
		try{
			Scanner in = new Scanner(new FileReader(loadDialog.fileLocation.getText()));
			while(in.hasNext()){
				String line = in.nextLine();
				parse(line.replace(System.lineSeparator(), "\n"));
			}
		}
		catch(Exception e){
			log.append(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean saveData(){
		try{
			Writer writer = new FileWriter(saveDialog.locationArea.getText());
			log.append("Writing to " + saveDialog.locationArea.getText()
					+ "...\n");
			ListIterator<String> iterator = packets.listIterator();
			
			while(iterator.hasNext()){
				writer.write(iterator.next().replace("\n", System.lineSeparator()));

			}
			
			writer.close();
		}
		catch(Exception e){
			log.append("File not saved\n");
			log.append(e.getMessage());
			return false;
		}
		
		
		return true;
	}
	
	public void clearData(){
		Platform.runLater(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				charts.tempSeries.getData().clear();
				charts.altiSeries.getData().clear();
				charts.xSeries.getData().clear();
				charts.ySeries.getData().clear();
				charts.zSeries.getData().clear();
				packets.clear();
				
				log.append("Data cleared\n");
			}
		});
	}
	
	private void sortData(String[] data){
		for(int i=1; i < data.length; i++){
			String field = data[i];
			String[] piece = field.split(":"); //separate type declaration from data
			String typeDec = piece[0];
			float theData = Float.parseFloat(piece[1]); 
			
			switch(typeDec) {
			case "TIME": lastPacketTime = theData;
						 break;
			case "TEMP": charts.updateTemp(lastPacketTime, theData);
						 break;
			case "ALTI": charts.updateAlti(lastPacketTime, Float.parseFloat(piece[1]));
						 break;
			case "XACC": charts.updateX(lastPacketTime, Float.parseFloat(piece[1]));
						 break;
			case "YACC": charts.updateY(lastPacketTime, Float.parseFloat(piece[1]));
				 		 break;
			case "ZACC": charts.updateZ(lastPacketTime, Float.parseFloat(piece[1]));
				 		 break;
			case "MSG":  log.append(piece[1] + "\n");
						 break;
			case "LAT":  //figure out what to do with GPS data;
						 break;
			case "LON":  //figure out what to do with GPS data;
				 		 break;
			}
		}
	}
}
