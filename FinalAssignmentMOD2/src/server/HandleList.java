package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HandleList {

	private DatagramSocket listSocket;
	private File fileDirectory;
	private InetAddress clientAddress;
	private int clientPort;

	public HandleList(DatagramSocket listSocket, File fileDirectory, InetAddress clientAddress, int clientPort) {
		this.listSocket = listSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
	}

	public void start() {
		String[] listOfFiles = fileDirectory.list();

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(listOfFiles);
			objectOutputStream.flush();
			objectOutputStream.close();

			byte[] listOfFilesBytes = byteArrayOutputStream.toByteArray();		
			DatagramPacket listResponse = new DatagramPacket(listOfFilesBytes, listOfFilesBytes.length, clientAddress, clientPort);
			listSocket.send(listResponse);
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}
	}
}
