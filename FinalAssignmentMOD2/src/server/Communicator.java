package server;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Communicator implements Runnable {
	
	private FileServer fileServer;
	private DatagramSocket communicationSocket;
	
	private InetAddress clientAddress;
	private int clientPort;

	public Communicator(FileServer fileServer, DatagramSocket communicationSocket, 
			DatagramPacket connectRequest) {
		this.fileServer = fileServer;
		this.communicationSocket = communicationSocket;
		
		clientAddress = connectRequest.getAddress();
		clientPort = connectRequest.getPort();
	}

	@Override
	public void run() {
		try {
			sendMaxNameLengthAndPortNumbers();
			while (true) {
				fileServer.handleUpload();
				fileServer.handleDownload(clientAddress, clientPort);
				fileServer.handleRemove();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void sendMaxNameLengthAndPortNumbers()
			throws IOException {
		byte[] maxNameBytes = BigInteger.valueOf(FileServer.MAX_NAME_LENGTH).toByteArray();
		byte[] uploadPortBytes = BigInteger.valueOf(FileServer.UPLOAD_PORT).toByteArray();
		byte[] downloadPortBytes = BigInteger.valueOf(FileServer.DOWNLOAD_PORT).toByteArray();
		byte[] listPortBytes = BigInteger.valueOf(FileServer.LIST_PORT).toByteArray();		
		
		byte[] buffOut = new byte[7];
		System.arraycopy(maxNameBytes, 0, buffOut, 0, maxNameBytes.length);
		System.arraycopy(uploadPortBytes, 0, buffOut, maxNameBytes.length, uploadPortBytes.length);
		System.arraycopy(downloadPortBytes, 0, buffOut, maxNameBytes.length + uploadPortBytes.length, 
				downloadPortBytes.length);
		System.arraycopy(listPortBytes, 0, buffOut, maxNameBytes.length + uploadPortBytes.length + 
				downloadPortBytes.length, listPortBytes.length);
		
		DatagramPacket sendMaxNameLengthAndPortNumbers = new DatagramPacket(buffOut, buffOut.length, clientAddress, clientPort);
		communicationSocket.send(sendMaxNameLengthAndPortNumbers);
	}
}
