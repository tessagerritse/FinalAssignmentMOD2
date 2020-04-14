package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import shared.Protocol;

public class FileServer {
	
	private File fileDirectory;
	
	private DatagramSocket metaSocket;
	private DatagramSocket uploadSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket removeSocket;
	private DatagramSocket listSocket;

	private int clientMetaPort;

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
			setupHandlers();
			System.out.println("The server is started en waiting for clients to connect. \n");
			while (true) {
				connectClient();
			}
		} catch (SocketException e) {
			System.out.println("Socket exception at server-setup: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO exception at connecting with client: " + e.getMessage());
		}
	}
	
	private void connectClient() throws IOException {
		DatagramPacket connectRequest = new DatagramPacket(new byte[1], 1);
		metaSocket.receive(connectRequest);
		
		clientMetaPort = connectRequest.getPort(); 
		
		String feedback = "You are now connected. \n";
		byte[] feedbackBytes = feedback.getBytes();
		DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, connectRequest.getAddress(), clientMetaPort);
		metaSocket.send(feedbackPacket);
	}

	private void setupHandlers() {
		UploadHandler uploadhandler = new UploadHandler(uploadSocket, metaSocket, fileDirectory, clientMetaPort);
		new Thread(uploadhandler).start();
		
		DownloadHandler downloadhandler = new DownloadHandler(downloadSocket, metaSocket, fileDirectory, clientMetaPort);
		new Thread(downloadhandler).start();
		
		RemoveHandler removehandler = new RemoveHandler(removeSocket, metaSocket, fileDirectory, clientMetaPort);
		new Thread(removehandler).start();
		
		ListHandler listhandler = new ListHandler(listSocket, metaSocket, fileDirectory, clientMetaPort);
		new Thread(listhandler).start();
	}

	private void setupSockets() throws SocketException {
		metaSocket = new DatagramSocket(Protocol.META_PORT);
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
