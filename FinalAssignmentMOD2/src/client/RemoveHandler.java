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
	private int communicationPort;
	private File fileDirectory;
	private String fileName;

	public RemoveHandler(ClientTUI view, DatagramSocket clientSocket, InetAddress serverAddress, int communicationPort,
			File fileDirectory, String fileName) {
		this.view = view;
		this.clientSocket = clientSocket;
		this.serverAddress = serverAddress;
		this.communicationPort = communicationPort;
		this.fileDirectory = fileDirectory;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			byte[] fileNameBytes = fileName.getBytes();
			DatagramPacket removeRequest = new DatagramPacket(fileNameBytes, fileNameBytes.length, serverAddress, communicationPort);
			clientSocket.send(removeRequest);
		} catch (IOException e) {
			view.showMessage("IO error: " + e.getMessage());
		}
	}

}
