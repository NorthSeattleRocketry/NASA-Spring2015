package receiver;

public class Packet {
	public String time, altitude, temperature, latitude, longitude, xAcc, 
		yAcc, zAcc;
	
	public Packet(String tim, String alti, String temp, String lat,
			String lon, String xAc, String yAc, String zAc){
		time = tim;
		temperature = temp;
		altitude = alti;
		latitude = lat;
		longitude = lon;
		xAcc = xAc;
		yAcc = yAc;
		zAcc = zAc;
	}

	public Packet(String tim, String alti, String temp, String xAc, String yAc, String zAc){
		time = tim;
		temperature = temp;
		altitude = alti;
		xAcc = xAc;
		yAcc = yAc;
		zAcc = zAc;
	}
}
