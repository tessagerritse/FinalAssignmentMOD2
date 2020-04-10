package server;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Communicator implements Runnable {
	
	private FileServer fileServer;
	private DatagramSocket communicationSocket;
	private int uploadPort;
	private int downloadPort;
	private int listPort;
	
	private InetAddress clientAddress;
	private int clientPort;

	public Communicator(FileServer fileServer, DatagramSocket communicationSocket, 
			int uploadPort, int downloadPort, int listPort, DatagramPacket connectRequest) {
		this.fileServer = fileServer;
		this.communicationSocket = communicationSocket;
		this.uploadPort = uploadPort;
		this.downloadPort = downloadPort;
		this.listPort = listPort;
		
		clientAddress = connectRequest.getAddress();
		clientPort = connectRequest.getPort();
	}

	@Override
	public void run() {
		try {
			sendPortNumbers();
			while (true) {
				fileServer.handleUpload();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void sendPortNumbers()
			throws IOException {
		byte[] buffer0 = BigInteger.valueOf(uploadPort).toByteArray();
		DatagramPacket sendUploadPort0 = new DatagramPacket(buffer0, buffer0.length, clientAddress, 
				clientPort);
		communicationSocket.send(sendUploadPort0);
		
		byte[] buffer1 = BigInteger.valueOf(downloadPort).toByteArray();
		DatagramPacket sendUploadPort1 = new DatagramPacket(buffer1, buffer1.length, clientAddress, 
				clientPort);
		communicationSocket.send(sendUploadPort1);
		
		byte[] buffer2 = BigInteger.valueOf(listPort).toByteArray();
		DatagramPacket sendUploadPort2 = new DatagramPacket(buffer2, buffer2.length, clientAddress, 
				clientPort);
		communicationSocket.send(sendUploadPort2);
	}
}
