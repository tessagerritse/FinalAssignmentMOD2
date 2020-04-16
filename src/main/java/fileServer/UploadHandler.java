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
 * Receives a file from a client and saves it on the server.
 * 
 * @author tessa.gerritse
 *
 */
public class UploadHandler implements Runnable {

	private DatagramSocket uploadSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public UploadHandler(DatagramSocket uploadSocket, DatagramSocket metaSocket, File fileDirectory, 
			InetAddress clientAddress) {
		this.uploadSocket = uploadSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	/**
	 * Continually listens for a name packet, receives a name packet and file packet(s) if the arrive, 
	 * unpacks them, and saves the file
	 */
	public void run() {
		while (true) {
			try {	
				byte[] fileNameBytes  = Receiver.receiveName(uploadSocket, clientAddress, 
						Protocol.CLIENT_UPLOAD_PORT);						
				byte[] fileContentBytes = Receiver.receiveMultiplePackets(uploadSocket, clientAddress, 
						Protocol.CLIENT_UPLOAD_PORT);
				
				String fileName = DataActions.getStringFromBytes(fileNameBytes);				
				File file = DataActions.getFileObject(fileDirectory, fileName);
				
				String feedback;
				if (!file.exists()) {
					feedback = "File " + fileName + " is saved on the server. \n";
				} else {
					feedback = "File " + fileName + " already existed and is thus overwritten. \n";
				}

				DataActions.writeFileContentToDirectory(file, fileContentBytes);
				
				byte[] feedbackBytes = DataActions.getBytesFromString(feedback);
				Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
			} catch (IOException e) {
				System.out.println("IO exception at upload handler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}