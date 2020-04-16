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
 * Sends a file to the client when the user requests a certain file-download.
 * 
 * @author tessa.gerritse
 *
 */
public class DownloadHandler implements Runnable {

	private DatagramSocket downloadSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public DownloadHandler(DatagramSocket downloadSocket, DatagramSocket metaSocket, File fileDirectory, 
			InetAddress clientAddress) {
		this.downloadSocket = downloadSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	/**
	 * Continually listens for a name packet to arrive and then sends the requested file or feedback 
	 * if the file does not exist on the server.
	 */
	public void run() {
		while (true) {
			try {
				byte[] nameBytes = Receiver.receiveName(downloadSocket, clientAddress, 
						Protocol.CLIENT_DOWNLOAD_PORT);
				String fileName = DataActions.getStringFromBytes(nameBytes);
				
				File file = DataActions.getFileObject(fileDirectory, fileName);
				
				if (!DataActions.exists(file)) {
					String feedback = "File " + fileName + " doesn't exist on server. \n";
					byte[] feedbackBytes = DataActions.getBytesFromString(feedback);
					Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
				} else {
					byte[] fileContentBytes = DataActions.getFileContent(file);
					Sender.sendSingleOrMultiplePackets(downloadSocket, clientAddress, 
							Protocol.CLIENT_DOWNLOAD_PORT, fileContentBytes);
					String feedback = "Sent file " + fileName + "\n";
					byte[] feedbackBytes = DataActions.getBytesFromString(feedback);
					Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
				}		
			} catch (IOException e) {
				System.out.println("IO exception at download handler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}	
}
