package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.DataActions;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

public class DownloadHandler implements Runnable {

	private DatagramSocket downloadSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public DownloadHandler(DatagramSocket downloadSocket, DatagramSocket metaSocket, File fileDirectory, InetAddress clientAddress) {
		this.downloadSocket = downloadSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] nameBytes = Receiver.receiveName(downloadSocket);
				String fileName = DataActions.getStringFromBytes(nameBytes);
				
				File file = DataActions.getFileObject(fileDirectory, fileName);
				
				String feedback;
				if (!DataActions.exists(file)) {
					file.delete();
					feedback = "File " + fileName + " doesn't exist on server. \n";
				} else {
					byte[] fileContentBytes = DataActions.getFileContent(file);
					Sender.sendFilePacket(downloadSocket, clientAddress, Protocol.CLIENT_DOWNLOAD_PORT, fileContentBytes);
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
