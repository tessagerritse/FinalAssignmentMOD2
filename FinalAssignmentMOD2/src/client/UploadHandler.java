package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import transmission.ProtocolMessages;

public class UploadHandler implements Runnable {

	private String path = "/Users/tessa.gerritse/git/FinalAssignmentMOD2/FinalAssignmentMOD2/src/client"; //TODO automatisch de path van de file bepalen

	FileClientTUI view;
	DatagramSocket clientSocket;
	InetAddress serverAddress;
	int uploadPort;
	String fileName;

	public UploadHandler(FileClientTUI view, DatagramSocket clientSocket, InetAddress serverAddress, int uploadPort, String fileName) {
		this.clientSocket = clientSocket;
		this.serverAddress = serverAddress;
		this.uploadPort = uploadPort;
		this.fileName = fileName;
		this.view = view;
	}

	@Override
	public void run() {		
		try {			
			InputStream inputStream = new FileInputStream(path + fileName); 
			byte[] buffOut = inputStream.readAllBytes();

			DatagramPacket request = new DatagramPacket(buffOut, buffOut.length, serverAddress, uploadPort);
			clientSocket.send(request);
			System.out.println("sent upload packet");

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
