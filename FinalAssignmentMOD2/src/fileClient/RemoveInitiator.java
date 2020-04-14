package fileClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.Protocol;

public class RemoveInitiator implements Runnable {

	private FileClientTUI view;
	private DatagramSocket removeSocket;
	private InetAddress serverAddress;
	private String fileName;

	public RemoveInitiator(FileClientTUI view, DatagramSocket removeSocket, InetAddress serverAddress,
			String fileName) {
		this.view = view;
		this.removeSocket = removeSocket;
		this.serverAddress = serverAddress;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			byte[] nameBytes = fileName.getBytes();
			DatagramPacket namePacket = new DatagramPacket(nameBytes, nameBytes.length, serverAddress, Protocol.REMOVE_PORT);
			removeSocket.send(namePacket);
			
			view.showMessage("Sent request to server to remove " + fileName + ". \n");
		} catch (IOException e) {
			view.showMessage("IO exception at remove initiator: " + e.getMessage());
		}
	}

}
