package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.DataActions;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

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
				
				String feedback;
				if (!DataActions.exists(file)) {
					feedback = "File " + fileName + " doesn't exist on server. \n";
				} else {
					byte[] fileContentBytes = DataActions.getFileContent(file);
					Sender.sendSingleOrMultiplePackets(downloadSocket, clientAddress, 
							Protocol.CLIENT_DOWNLOAD_PORT, fileContentBytes);
					feedback = "Sent file " + fileName + "\n";
				}
				byte[] feedbackBytes = DataActions.getBytesFromString(feedback);
				Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);		
			} catch (IOException e) {
				System.out.println("IO exception at download handler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}	
}
