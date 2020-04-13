package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HandleDownload {

	private DatagramSocket downloadSocket;
	private File fileDirectory;
	private InetAddress clientAddress;
	private int clientPort;

	public HandleDownload(DatagramSocket downloadSocket, File fileDirectory, InetAddress clientAddress, int clientPort) {
		this.downloadSocket = downloadSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
	}

	public void start() {
		try {
			byte[] fileNameBytes = new byte[FileServer.MAX_NAME_LENGTH];
			DatagramPacket downloadRequest = new DatagramPacket(fileNameBytes, fileNameBytes.length);
			downloadSocket.receive(downloadRequest);
			
			String fileName = new String(fileNameBytes).trim();
			
			File file = new File(fileDirectory + "/" + fileName);
			Path path = Paths.get(file.toURI());
			if (file.createNewFile()) {
				Files.delete(path);
				String feedback = fileName + " doesn't exist on server.";
				
				System.out.println(feedback);
				
				byte[] feedbackBytes = feedback.getBytes();
				DatagramPacket failedDownload = new DatagramPacket(feedbackBytes, feedbackBytes.length, clientAddress, clientPort);
				downloadSocket.send(failedDownload);
			} else {
				URI uri = file.toURI();				
				byte[] fileContentBytes = Files.readAllBytes(path);
				DatagramPacket downloadResponse = new DatagramPacket(fileContentBytes, fileContentBytes.length, clientAddress, clientPort);
				downloadSocket.send(downloadResponse);
			}
		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		}
	}
}
