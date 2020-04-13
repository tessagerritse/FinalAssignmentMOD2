package server;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import transmission.ProtocolMessages;

public class Communicator implements Runnable {
	
	private Server fileServer;
	private DatagramSocket communicationSocket;
	
	private InetAddress clientAddress;
	private int clientPort;

	public Communicator(Server fileServer, DatagramSocket communicationSocket, 
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
				handleClientRequest();				
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handleClientRequest() throws IOException {
		byte[] requestByte = new byte[1];		
		DatagramPacket request = new DatagramPacket(requestByte, requestByte.length);
		communicationSocket.receive(request);
		
		System.out.println("Received request");
		
		String command = new String(requestByte).trim();
		
		System.out.println("The command is: " + command);
		
		switch (command) {
		case ProtocolMessages.UPLOAD:
			
			System.out.println("Commanding server to upload the file");
			
			fileServer.handleUpload();
			break;
		case ProtocolMessages.DOWNLOAD:
			fileServer.handleDownload(clientAddress, clientPort);
			break;
		case ProtocolMessages.REMOVE:
			fileServer.handleRemove();
			break;
		case ProtocolMessages.LIST:
			fileServer.handleList(clientAddress, clientPort);
			break;
		}
	}

	private void sendMaxNameLengthAndPortNumbers()
			throws IOException {
		byte[] maxNameBytes = BigInteger.valueOf(Server.MAX_NAME_LENGTH).toByteArray();
		byte[] uploadPortBytes = BigInteger.valueOf(Server.UPLOAD_PORT).toByteArray();
		byte[] downloadPortBytes = BigInteger.valueOf(Server.DOWNLOAD_PORT).toByteArray();
		byte[] removePortBytes = BigInteger.valueOf(Server.REMOVE_PORT).toByteArray();
		byte[] listPortBytes = BigInteger.valueOf(Server.LIST_PORT).toByteArray();		
		
		byte[] buffOut = new byte[9];
		System.arraycopy(maxNameBytes, 0, buffOut, 0, maxNameBytes.length);
		System.arraycopy(uploadPortBytes, 0, buffOut, maxNameBytes.length, uploadPortBytes.length);
		System.arraycopy(downloadPortBytes, 0, buffOut, maxNameBytes.length + uploadPortBytes.length, 
				downloadPortBytes.length);
		System.arraycopy(removePortBytes, 0, buffOut, maxNameBytes.length + uploadPortBytes.length + 
				downloadPortBytes.length, removePortBytes.length);
		System.arraycopy(listPortBytes, 0, buffOut, maxNameBytes.length + uploadPortBytes.length + 
				downloadPortBytes.length + removePortBytes.length, listPortBytes.length);
		
		DatagramPacket sendMaxNameLengthAndPortNumbers = new DatagramPacket(buffOut, buffOut.length, clientAddress, clientPort);
		communicationSocket.send(sendMaxNameLengthAndPortNumbers);
	}
}
