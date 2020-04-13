package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HandleRemove {

	private DatagramSocket removeSocket;
	private File fileDirectory;

	public HandleRemove(DatagramSocket removeSocket, File fileDirectory) {
		this.removeSocket = removeSocket;
		this.fileDirectory = fileDirectory;
	}

	public void start() {
		
		try {
			byte[] fileNameBytes = new byte[Server.MAX_NAME_LENGTH];
			DatagramPacket removeRequest = new DatagramPacket(fileNameBytes, fileNameBytes.length);
			removeSocket.receive(removeRequest);
			
			String fileName = new String(fileNameBytes).trim();
			
			System.out.println("User wants to remove " + fileName);
			
			File file = new File(fileDirectory + "/" + fileName);
			
			//File f = new File(System.getProperty("user.home") + "/FilesOnServer" + "/tiny.pdf");
			if(!file.exists()) { 
			   System.out.println("Doesn't exist");
			} else if (!file.isDirectory()) {
				System.out.println("Is a directory");
			} else if (!file.canWrite()) {
				System.out.println("No write permissions");
			} else {
				file.delete();
			}
			
			if(file.delete()) { 
	            System.out.println("File deleted successfully"); 
	        } else { 
	            System.out.println("Failed to delete the file"); 
	        } 
			
			
			
			
//			Path path = Paths.get(file.toURI());
//			Files.delete(path);
			
			
			
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}

		
	}
}
