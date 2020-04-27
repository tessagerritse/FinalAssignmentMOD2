package fileClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

import shared.Utils;
import shared.Protocol;
import shared.Sender;

/**
 * Uploads a file to the server when user commands.
 * 
 * @author tessa.gerritse
 *
 */
public class UploadInitiator implements Runnable {
	
	private final FileClientTUI view;
	private final DatagramSocket uploadSocket;
	private final InetAddress serverAddress;
	private final File fileDirectory;
	private final String fileName;
	
	public UploadInitiator(FileClientTUI view, DatagramSocket uploadSocket, InetAddress serverAddress,
			File fileDirectory, String fileName) {	
		this.view = view;
		this.uploadSocket = uploadSocket;
		this.serverAddress = serverAddress;
		this.fileDirectory = fileDirectory;
		this.fileName = fileName;
	}

	@Override
	/**
	 * Sends file name and file to server, receives feedback if that file has overwritten another 
	 * file with the same name or not, and displays the feedback.
	 */
	public void run() {
		try {	
			File file = Utils.getFileObject(fileDirectory, fileName);
			
			if (!file.exists()) {
				view.showMessage("File " + fileName + " doesn't exist in the directory. Please try again. \n");
			} else {				
				byte[] fileNameBytes = Utils.getBytesFromString(fileName);
				Instant start = Instant.now();
				Sender.sendNamePacket(uploadSocket, serverAddress, Protocol.UPLOAD_PORT, fileNameBytes);

				byte[] fileContentBytes = Utils.getFileContent(file);
				Sender.sendSingleOrMultiplePackets(uploadSocket, serverAddress, Protocol.UPLOAD_PORT, 
						fileContentBytes);
				
				Instant end = Instant.now();
				Duration timeElapsed = Duration.between(start, end); 
				
				view.showMessage("File " + fileName + " is uploaded to the server. "
						+ "It took " + timeElapsed.toMillis() + " milliseconds \n");
			}	
		} catch (IOException e) {
			view.showMessage("IO exception at upload initiator " + e.getMessage());
		}	
	}
}
