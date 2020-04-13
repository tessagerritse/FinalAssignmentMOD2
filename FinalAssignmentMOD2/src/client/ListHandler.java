package client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListHandler implements Runnable {

	private ClientTUI view;
	private DatagramSocket clientSocket;

	public ListHandler(ClientTUI view, DatagramSocket clientSocket) {
		this.view = view;
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try {
			byte[] listOfFilesBytes = new byte[25000];
			DatagramPacket listResponse = new DatagramPacket(listOfFilesBytes, listOfFilesBytes.length);
			clientSocket.receive(listResponse);
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(listOfFilesBytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			String[] listOfFiles = (String[]) objectInputStream.readObject();
			objectInputStream.close();
			
			for (String file : listOfFiles) {
				view.showMessage(file);
			}
		} catch (IOException e) {
			view.showMessage("IO error: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			view.showMessage("ClassNotFountErrror: " + e.getMessage());
		}
	}
}
