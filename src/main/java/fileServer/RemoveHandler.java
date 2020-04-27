package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import sender.FeedbackSender;
import shared.Utils;
import shared.Protocol;
import shared.Receiver;

/**
 * Removes a file from the server when user commands to do so.
 * 
 * @author tessa.gerritse
 *
 */
public class RemoveHandler implements Runnable {

	private final DatagramSocket removeSocket;
	private final DatagramSocket metaSocket;
	private final File fileDirectory;
	private final InetAddress clientAddress;

	private boolean listenForRemoves = true;

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
		while (listenForRemoves) {
			try {
				byte[] nameBytes = Receiver.receiveName(removeSocket, clientAddress, 
						Protocol.CLIENT_REMOVE_PORT);
				String fileName = Utils.getStringFromBytes(nameBytes);
				File file = Utils.getFileObject(fileDirectory, fileName);

				String feedback;
				if (!file.exists()) {
					feedback = "File " + fileName + " is not one of the files on server. \n";
					byte[] feedbackBytes = feedback.getBytes();
					(new FeedbackSender()).send(metaSocket, clientAddress, Protocol.CLIENT_META_PORT, feedbackBytes);
				} else {
					if(file.delete()) { 
						feedback = "File " + fileName + " deleted successfully"; 
			        } else { 
			        	feedback = "Failed to delete " + fileName + ". Reason unknown."; 
			        } 
					byte[] feedbackBytes = feedback.getBytes();
					(new FeedbackSender()).send(metaSocket, clientAddress, Protocol.CLIENT_META_PORT, feedbackBytes);
				}
			} catch (IOException e) {
				setListenForRemoves(false);
				System.out.println("IO exception at upload handler: " + e.getMessage());
			}
		}
	}
	public void setListenForRemoves(boolean listenForRemoves) {
		this.listenForRemoves = listenForRemoves;
	}
}
