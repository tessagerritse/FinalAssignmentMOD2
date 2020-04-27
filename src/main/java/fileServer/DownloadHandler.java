package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.Utils;
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

	private final DatagramSocket downloadSocket;
	private final DatagramSocket metaSocket;
	private final File fileDirectory;
	private final InetAddress clientAddress;

	private boolean listenForDownloads = true;

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
		while (listenForDownloads) {
			try {
				byte[] nameBytes = Receiver.receiveName(downloadSocket, clientAddress,
						Protocol.CLIENT_DOWNLOAD_PORT);
				String fileName = Utils.getStringFromBytes(nameBytes);

				File file = Utils.getFileObject(fileDirectory, fileName);

				if (!file.exists()) {
					String feedback = "File " + fileName + " doesn't exist on server. \n";
					byte[] feedbackBytes = feedback.getBytes();
					Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
				} else {
					byte[] fileContentBytes = Utils.getFileContent(file);
					Sender.sendSingleOrMultiplePackets(downloadSocket, clientAddress,
							Protocol.CLIENT_DOWNLOAD_PORT, fileContentBytes);
					String feedback = "Sent file " + fileName + "\n";
					byte[] feedbackBytes = feedback.getBytes();
					Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
				}
			} catch (IOException e) {
				setListenForDownloads(false);
				System.out.println("IO exception at download handler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}


	public void setListenForDownloads(boolean listenForDownloads) {
		this.listenForDownloads = listenForDownloads;
	}
}