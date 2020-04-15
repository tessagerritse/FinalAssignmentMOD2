package fileServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.FileActions;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

public class UploadHandler implements Runnable {

	private DatagramSocket uploadSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public UploadHandler(DatagramSocket uploadSocket, DatagramSocket metaSocket, File fileDirectory, InetAddress clientAddress) {
		this.uploadSocket = uploadSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	public void run() {
		while (true) {
			try {	
				byte[] fileNameBytes  = Receiver.receiveName(uploadSocket, clientAddress);						
				byte[] fileContentBytes = Receiver.receiveFile(uploadSocket, clientAddress, Protocol.CLIENT_UPLOAD_PORT);
				
				String fileName = FileActions.getStringFromBytes(fileNameBytes);				
				File file = FileActions.getFileObject(fileDirectory, fileName);
				
				String feedback;
				if (!file.exists()) {
					feedback = "File " + fileName + " is saved on the server. \n";
				} else {
					feedback = "File " + fileName + " already existed and is thus overwritten. \n";
				}

				FileActions.writeFileContentToDirectory(file, fileContentBytes);
				
				byte[] feedbackBytes = FileActions.getBytesFromString(feedback);
				Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
			} catch (IOException e) {
				System.out.println("IO exception at upload handler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
