package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import server.FileServer;

public class DownloadHandler implements Runnable {

	private ClientTUI view;
	private DatagramSocket clientSocket;
	private InetAddress serverAddress;
	private int downloadPort;
	private File fileDirectory;
	private int maxNameLength;
	private String fileName;

	public DownloadHandler(ClientTUI view, DatagramSocket clientSocket, InetAddress serverAddress, int downloadPort, File fileDirectory, int maxNameLength, String fileName) {
		this.view = view;
		this.clientSocket = clientSocket;
		this.serverAddress = serverAddress;
		this.downloadPort = downloadPort;
		this.fileDirectory = fileDirectory;
		this.maxNameLength = maxNameLength;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			byte[] fileNameBytes = fileName.getBytes();
			DatagramPacket downloadRequest = new DatagramPacket(fileNameBytes, maxNameLength, serverAddress, downloadPort);
			clientSocket.send(downloadRequest);
			
			byte[] buffIn = new byte[26000];
			DatagramPacket downloadFile = new DatagramPacket(buffIn, buffIn.length);
			clientSocket.receive(downloadFile);
		} catch (IOException e) {
			view.showMessage("IO error: " + e.getMessage());
		}		
	}
}
