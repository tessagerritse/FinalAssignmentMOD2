package client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RemoveHandler implements Runnable {

	private ClientTUI view;
	private DatagramSocket clientSocket;
	private InetAddress serverAddress;
	private int removePort;
	private String fileName;

	public RemoveHandler(ClientTUI view, DatagramSocket clientSocket, InetAddress serverAddress, int removePort, String fileName) {
		this.view = view;
		this.clientSocket = clientSocket;
		this.serverAddress = serverAddress;
		this.removePort = removePort;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			byte[] fileNameBytes = fileName.getBytes();
			DatagramPacket removeRequest = new DatagramPacket(fileNameBytes, fileNameBytes.length, serverAddress, removePort);
			clientSocket.send(removeRequest);
			
			System.out.println("Sent remove of " + fileName + " to server");
			
		} catch (IOException e) {
			view.showMessage("IO error: " + e.getMessage());
		}
	}

}
