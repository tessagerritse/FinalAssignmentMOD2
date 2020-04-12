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
				fileServer.handleDownload();
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
		
		
//		byte[] buffer0 = BigInteger.valueOf(uploadPort).toByteArray();
//		DatagramPacket sendUploadPort0 = new DatagramPacket(buffer0, buffer0.length, clientAddress, 
//				clientPort);
//		communicationSocket.send(sendUploadPort0);
//		
//		byte[] buffer1 = BigInteger.valueOf(downloadPort).toByteArray();
//		DatagramPacket sendUploadPort1 = new DatagramPacket(buffer1, buffer1.length, clientAddress, 
//				clientPort);
//		communicationSocket.send(sendUploadPort1);
//		
//		byte[] buffer2 = BigInteger.valueOf(listPort).toByteArray();
//		DatagramPacket sendUploadPort2 = new DatagramPacket(buffer2, buffer2.length, clientAddress, 
//				clientPort);
//		communicationSocket.send(sendUploadPort2);
	}
}
