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
 * Receives a file from a client and saves it on the server.
 * 
 * @author tessa.gerritse
 *
 */
public class UploadHandler implements Runnable {

	private final DatagramSocket uploadSocket;
	private final DatagramSocket metaSocket;
	private final File fileDirectory;
	private final InetAddress clientAddress;

	private boolean listenForUploads = true;

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
		while (listenForUploads) {
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
					feedback = "File " + fileName + " was already in use and is thus overwritten. \n";
				}

				DataActions.writeFileContentToDirectory(file, fileContentBytes);
				
				byte[] feedbackBytes = DataActions.getBytesFromString(feedback);
				Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);				
			} catch (IOException e) {
				setListenForUploads(false);
				System.out.println("IO exception at upload handler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public void setListenForUploads(boolean listenForUploads) {
		this.listenForUploads = listenForUploads;
	}
}
