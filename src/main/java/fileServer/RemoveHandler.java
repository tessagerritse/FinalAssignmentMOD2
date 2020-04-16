package main.java.fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import main.java.shared.DataActions;
import main.java.shared.Protocol;
import main.java.shared.Receiver;
import main.java.shared.Sender;

/**
 * Removes a file from the server when user commands to do so.
 * 
 * @author tessa.gerritse
 *
 */
public class RemoveHandler implements Runnable {

	private DatagramSocket removeSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public RemoveHandler(DatagramSocket removeSocket, DatagramSocket metaSocket, File fileDirectory, 
			InetAddress clientAddress) {
		this.removeSocket = removeSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	/**
	 * Continually listens for a name packet, removes the indicated file if it exists and sends feedback 
	 * if the removal was successful or not.
	 */
	public void run() {
		while (true) {
			try {
				byte[] nameBytes = Receiver.receiveName(removeSocket, clientAddress, 
						Protocol.CLIENT_REMOVE_PORT);
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
