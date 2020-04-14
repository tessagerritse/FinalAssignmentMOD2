package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import shared.Protocol;

public class RemoveHandler implements Runnable {

	private DatagramSocket removeSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private int clientMetaPort;

	public RemoveHandler(DatagramSocket removeSocket, DatagramSocket metaSocket, File fileDirectory,
			int clientMetaPort) {
		this.removeSocket = removeSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientMetaPort = clientMetaPort;
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] nameBytes= new byte[Protocol.NAME_PACKET_SIZE];
				DatagramPacket namePacket = new DatagramPacket(nameBytes, nameBytes.length);
				removeSocket.receive(namePacket);

				String fileName = new String(namePacket.getData()).trim();

				File file = new File(fileDirectory + "/" + fileName);

				String feedback;
				if (!file.exists()) {
					feedback = "File " + fileName + " doesn't exist on server. \n";
				} else {
					if(file.delete()) { 
			            feedback = "File " + fileName + " deleted successfully"; 
			        } else { 
			            feedback = "Failed to delete " + fileName + ". Reason unknown."; 
			        } 
				}
				byte[] feedbackBytes = feedback.getBytes();
				DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, namePacket.getAddress(), clientMetaPort);
//				metaSocket.send(feedbackPacket);
				
				//TODO deze print verwijderen en in plaats daarvan meta werken krijgen
				System.out.println(feedback);
			} catch (IOException e) {
				System.out.println("IO exception at upload handler: " + e.getMessage());
			}
		}
	}

}
