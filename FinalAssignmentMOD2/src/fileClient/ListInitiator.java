package fileClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.Protocol;

public class ListInitiator implements Runnable {

	private FileClientTUI view;
	private DatagramSocket listSocket;
	private InetAddress serverAddress;

	public ListInitiator(FileClientTUI view, DatagramSocket listSocket, InetAddress serverAddress) {
		this.view = view;
		this.listSocket = listSocket;
		this.serverAddress = serverAddress;
	}

	@Override
	public void run() {
		try {
			byte[] listCommand = Protocol.LIST.getBytes();
			DatagramPacket listCommandPacket = new DatagramPacket(listCommand, listCommand.length, serverAddress, Protocol.LIST_PORT);
			listSocket.send(listCommandPacket);
			
			byte[] listOfFilesBytes = new byte[Protocol.FILE_PACKET_SIZE];
			DatagramPacket listOfFilesPacket = new DatagramPacket(listOfFilesBytes, listOfFilesBytes.length);
			listSocket.receive(listOfFilesPacket);
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(listOfFilesPacket.getData());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			String[] listOfFiles = (String[]) objectInputStream.readObject();
			objectInputStream.close();
			
			for (String file : listOfFiles) {
				view.showMessage(file);
			}
		} catch (IOException e) {
			view.showMessage("IO exception at list initiator: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			view.showMessage("ClassNotFound exception at list initiator: " + e.getMessage());
		}
	}

}
