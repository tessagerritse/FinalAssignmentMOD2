package main.java.fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import main.java.shared.Protocol;

/**
 * Listens for clients that want to connect and for user commands on several ports.
 * The several threads handle the user commands themselves.
 * 
 * @author tessa.gerritse
 *
 */
public class FileServer {
	
	private File fileDirectory;
	
	private DatagramSocket metaSocket;
	private DatagramSocket uploadSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket removeSocket;
	private DatagramSocket listSocket;
	
	private InetAddress clientAddress;

	public FileServer() {	
		fileDirectory = new File(System.getProperty("user.home") + "/FilesOnServer");
	}

	public static void main(String[] args) {
		(new FileServer()).start();
	}

	private void start() {
		try {
			setupDirectory();
			setupSockets();
			System.out.println("The server is started and waiting for clients to connect. \n");
				receiveConnectRequest();
				setupHandlers();
				sendConnectApproved();
				System.out.println("Client with hostname " + clientAddress.getHostName() + " just connected.");
		} catch (SocketException e) {
			System.out.println("Socket exception at server-setup: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO exception at connecting with client: " + e.getMessage());
		}
	}

	private void sendConnectApproved() throws IOException {		
		String feedback = "You are now connected. \n";
		byte[] feedbackBytes = feedback.getBytes();
		DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, 
				clientAddress, Protocol.CLIENT_META_PORT);
		metaSocket.send(feedbackPacket);
	}

	private void setupHandlers() {
		UploadHandler uploadhandler = new UploadHandler(uploadSocket, metaSocket, fileDirectory, 
				clientAddress);
		new Thread(uploadhandler).start();
		
		DownloadHandler downloadhandler = new DownloadHandler(downloadSocket, metaSocket, fileDirectory, 
				clientAddress);
		new Thread(downloadhandler).start();
		
		RemoveHandler removehandler = new RemoveHandler(removeSocket, metaSocket, fileDirectory, 
				clientAddress);
		new Thread(removehandler).start();
		
		ListHandler listhandler = new ListHandler(listSocket, metaSocket, fileDirectory, clientAddress);
		new Thread(listhandler).start();
	}
	
	private void receiveConnectRequest() throws IOException {
		DatagramPacket connectRequest = new DatagramPacket(new byte[1], 1);
		metaSocket.receive(connectRequest);
		
		clientAddress = connectRequest.getAddress();
	}

	private void setupSockets() throws SocketException {
//		metaSocket = new DatagramSocket(Protocol.META_PORT);
		uploadSocket = new DatagramSocket(Protocol.UPLOAD_PORT);
		downloadSocket = new DatagramSocket(Protocol.DOWNLOAD_PORT);
		removeSocket = new DatagramSocket(Protocol.REMOVE_PORT);
		listSocket = new DatagramSocket(Protocol.LIST_PORT);
	}

	private void setupDirectory() {
		if (!fileDirectory.exists()) {
			fileDirectory.mkdir();
	    }
	} 

}
