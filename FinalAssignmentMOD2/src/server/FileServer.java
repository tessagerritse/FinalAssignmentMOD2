package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import client.FileClient;

public class FileServer {

	private DatagramSocket communicationSocket;
	private DatagramSocket uploadSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket listSocket;

	private final int communicationPort = 8888;
	private final int uploadPort = 8008;
	private final int downloadPort = 8080;
	private final int listPort = 8800;

	private List<String> filesOnServer = new ArrayList<>();
	private String fileDirectory = "/Users/tessa.gerritse/OneDrive - Nedap/Documents/University/"
			+ "MOD2 (MOD3 UT)/FinalAssignment/Files/";

	public FileServer() {		
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
		communicationSocket = new DatagramSocket(communicationPort);
		uploadSocket = new DatagramSocket(uploadPort);
		downloadSocket = new DatagramSocket(downloadPort);
		listSocket = new DatagramSocket(listPort);
		System.out.println("Connect to portnumber " + communicationPort + " to work with this server.");
	}

	private void connectClient() throws IOException {
		DatagramPacket connectRequest = new DatagramPacket(new byte[1], 1);
		communicationSocket.receive(connectRequest);

		Communicator communicator = new Communicator(this, communicationSocket, uploadPort, 
				downloadPort, listPort, connectRequest);
		new Thread(communicator).start();
	}

	public synchronized void handleUpload() throws IOException {
		byte[] buffIn = new byte[25000];
		DatagramPacket uploadRequest = new DatagramPacket(buffIn, buffIn.length);
		uploadSocket.receive(uploadRequest);		
		
		String fileName = "File" + filesOnServer.size();
		File file = new File(fileDirectory + fileName);
		OutputStream outputStream = new FileOutputStream(file);
		outputStream.write(uploadRequest.getData());
		outputStream.close();
		System.out.println(fileName + " has just been uploaded and saved");
		filesOnServer.add(fileName);		
	}
}