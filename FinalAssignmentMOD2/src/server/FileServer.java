package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import client.FileClient;

public class FileServer {
	
	public static final int MAX_NAME_LENGTH = 25;
	public static final int COMMUNICATION_PORT = 8888;
	public static final int UPLOAD_PORT = 8008;
	public static final int DOWNLOAD_PORT = 8080;
	public static final int LIST_PORT = 8800;
	
	private DatagramSocket communicationSocket;
	private DatagramSocket uploadSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket listSocket;

	private List<String> filesOnServer;
	private File fileDirectory;
	Communicator communicator;

	public FileServer() {	
		filesOnServer = new ArrayList<>();
		fileDirectory = new File(System.getProperty("user.home") + "/FilesOnServer");
	}

	public static void main(String[] args) {
		(new FileServer()).start();
	} 

	private void start() {
		try {
			setup();
			while (true) {
				connectClient();
			}
		} catch (SocketException e) {
			System.out.println("Socket error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}
	}

	private void setup() throws SocketException {
		if (!fileDirectory.exists()) {
			fileDirectory.mkdir();
	    }
		
		communicationSocket = new DatagramSocket(COMMUNICATION_PORT);
		uploadSocket = new DatagramSocket(UPLOAD_PORT);
		downloadSocket = new DatagramSocket(DOWNLOAD_PORT);
		listSocket = new DatagramSocket(LIST_PORT);
		System.out.println("Connect to portnumber " + COMMUNICATION_PORT + " to work with this server. \n");
	}

	private void connectClient() throws IOException {
		DatagramPacket connectRequest = new DatagramPacket(new byte[1], 1);
		communicationSocket.receive(connectRequest);

		communicator = new Communicator(this, communicationSocket, connectRequest);
		new Thread(communicator).start();
	}

	public synchronized void handleUpload() {
		HandleUpload handleUpload = new HandleUpload(uploadSocket, fileDirectory, filesOnServer);
		handleUpload.start();
	}

	public synchronized void handleDownload(InetAddress clientAddress, int clientPort) {
		HandleDownload handleDownload = new HandleDownload(downloadSocket, fileDirectory, clientAddress, clientPort);
		handleDownload.start();
	}
}