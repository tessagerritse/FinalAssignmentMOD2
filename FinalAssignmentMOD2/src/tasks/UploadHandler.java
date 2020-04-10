package tasks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import client.FileClientTUI;
import transmission.ProtocolMessages;

public class UploadHandler implements Runnable {

	private String path = "/Users/tessa.gerritse/SSHome/eclipse-workplace/FinalAssignmentMOD2_KLAD/"
			+ "src/exampleFiles/"; //TODO automatisch de path van de file bepalen

	FileClientTUI view;
	DatagramSocket socket;
	InetAddress serverAddress;
	int port;
	String fileName;

	public UploadHandler(FileClientTUI view, DatagramSocket socket, InetAddress serverAddress, int port, String fileName) {
		this.socket = socket;
		this.serverAddress = serverAddress;
		this.port = port;
		this.fileName = fileName;
		this.view = view;
	}

	@Override
	public void run() {		
		try {			
			InputStream inputStream = new FileInputStream(path + fileName); 
			//TODO zorgen dat de fileName ook meegenomen wordt, zodat hij op de server dezelfde naam krijgt
			byte[] buffOut = inputStream.readAllBytes();

			DatagramPacket request = new DatagramPacket(buffOut, buffOut.length, serverAddress, port);
			socket.send(request);

//			byte[] buffIn = new byte[512];
//			DatagramPacket response = new DatagramPacket(buffIn, buffIn.length);
//			socket.receive(response);
//
//			String answer = new String(buffIn, 0, response.getLength());
//			System.out.println(answer);
//			System.out.println();
		} catch (FileNotFoundException e) {
			view.showMessage("File " + fileName + " could not be found.");
		} catch (IOException e) {
			view.showMessage(e.getMessage());
		} 
	}

	private void getPortNumber() {
		// TODO Auto-generated method stub
		
	}
}
