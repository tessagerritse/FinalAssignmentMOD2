package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HandleRemove {

	private DatagramSocket communicationSocket;
	private File fileDirectory;

	public HandleRemove(DatagramSocket communicationSocket, File fileDirectory) {
		this.communicationSocket = communicationSocket;
		this.fileDirectory = fileDirectory;
	}

	public void start() {
		
		try {
			byte[] fileNameBytes = new byte[FileServer.MAX_NAME_LENGTH];
			DatagramPacket removeRequest = new DatagramPacket(fileNameBytes, fileNameBytes.length);
			communicationSocket.receive(removeRequest);
			
			String fileName = new String(fileNameBytes).trim();
			File file = new File(fileDirectory + "/" + fileName);
			Path path = Paths.get(file.toURI());
			Files.delete(path);
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}

		
	}
}
