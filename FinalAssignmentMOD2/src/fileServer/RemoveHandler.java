package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import shared.DataActions;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

public class RemoveHandler implements Runnable {

	private DatagramSocket removeSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public RemoveHandler(DatagramSocket removeSocket, DatagramSocket metaSocket, File fileDirectory, InetAddress clientAddress) {
		this.removeSocket = removeSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] nameBytes = Receiver.receiveName(removeSocket);
				String fileName = DataActions.getStringFromBytes(nameBytes);
				File file = DataActions.getFileObject(fileDirectory, fileName);

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
				byte[] feedbackBytes = DataActions.getBytesFromString(feedback);
				Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
			} catch (IOException e) {
				System.out.println("IO exception at upload handler: " + e.getMessage());
			}
		}
	}

}
