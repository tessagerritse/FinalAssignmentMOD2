package main.java.fileClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.DataActions;
import shared.Sender;
import shared.Protocol;

/**
 * Uploads a file to the server when user commands.
 * 
 * @author tessa.gerritse
 *
 */
public class UploadInitiator implements Runnable {
	
	private FileClientTUI view;
	private DatagramSocket uploadSocket;
	private InetAddress serverAddress;
	private File fileDirectory;
	private String fileName;
	
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
			File file = DataActions.getFileObject(fileDirectory, fileName);
			
			if (!DataActions.exists(file)) {
				view.showMessage("File " + fileName + " doesn't exist in the directory. Please try again. \n");
			} else {
				byte[] fileNameBytes = DataActions.getBytesFromString(fileName);	
				Sender.sendNamePacket(uploadSocket, serverAddress, Protocol.UPLOAD_PORT, fileNameBytes);

				byte[] fileContentBytes = DataActions.getFileContent(file);	
				Sender.sendSingleOrMultiplePackets(uploadSocket, serverAddress, Protocol.UPLOAD_PORT, 
						fileContentBytes);
				
				view.showMessage("File " + fileName + " is uploaded to the server. \n");
			}	
		} catch (IOException e) {
			view.showMessage("IO exception at upload initiator " + e.getMessage());
		}	
	}
}
