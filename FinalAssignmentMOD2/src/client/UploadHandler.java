package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadHandler implements Runnable {
		
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
			URI uri = super.getClass().getResource(fileName).toURI();
			Path filePath = Paths.get(uri);
			byte[] buffOut = Files.readAllBytes(filePath);

			DatagramPacket request = new DatagramPacket(buffOut, buffOut.length, serverAddress, uploadPort);
			clientSocket.send(request);
			System.out.println("sent upload packet");
		} catch (FileNotFoundException e) {
			view.showMessage("File " + fileName + " could not be found.");
		} catch (IOException e) {
			view.showMessage("IO error: " + e.getMessage());
		} catch (URISyntaxException e) {
			view.showMessage("Could not convert URL to URI: " + e.getMessage());
		} 
	}
}
