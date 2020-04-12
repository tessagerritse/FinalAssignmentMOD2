package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HandleDownload {

	private DatagramSocket downloadSocket;
	private File fileDirectory;

	public HandleDownload(DatagramSocket downloadSocket, File fileDirectory) {
		this.downloadSocket = downloadSocket;
		this.fileDirectory = fileDirectory;
	}

	public void start() {
		try {
			byte[] fileNameBytes = new byte[FileServer.MAX_NAME_LENGTH];
			DatagramPacket downloadRequest = new DatagramPacket(fileNameBytes, fileNameBytes.length);
			downloadSocket.receive(downloadRequest);
			
			String fileName = new String(fileNameBytes).trim();
			URI uri = getClass().getResource(fileName).toURI();
			Path filePath = Paths.get(uri);
			byte[] fileContentBytes = Files.readAllBytes(filePath);		
			
						
		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		} catch (URISyntaxException e) {
			System.out.println("Error: Could not convert URL to URI: " + e.getMessage());

		}
	}

}
