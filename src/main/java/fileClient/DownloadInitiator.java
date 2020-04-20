package main.java.fileClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

import main.java.shared.DataActions;
import main.java.shared.Protocol;
import main.java.shared.Receiver;
import main.java.shared.Sender;

/**
 * Downloads a file from the server when user commands.
 * 
 * @author tessa.gerritse
 *
 */
public class DownloadInitiator implements Runnable {

	private final FileClientTUI view;
	private final DatagramSocket downloadSocket;
	private final InetAddress serverAddress;
	private final File fileDirectory;
	private final String fileName;
	private final MetaHandler metaHandler;

	public DownloadInitiator(FileClientTUI view, DatagramSocket downloadSocket, InetAddress serverAddress,
			File fileDirectory, String fileName, MetaHandler metaHandler) {
		this.view = view;
		this.downloadSocket = downloadSocket;
		this.serverAddress = serverAddress;
		this.fileDirectory = fileDirectory;
		this.fileName = fileName;
		this.metaHandler = metaHandler;
	}

	@Override
	/**
	 * Sends a file name, receives the indicated file, and saves it in local directory.
	 */
	public void run() {
		try {
			File file = DataActions.getFileObject(fileDirectory, fileName);

			if (file.exists()) {
				view.showMessage("File " + fileName + " already exists and will thus be overwritten. \n");
			}
			
			byte[] nameBytes = DataActions.getBytesFromString(fileName);
			Instant start = Instant.now();
			Sender.sendNamePacket(downloadSocket, serverAddress, Protocol.DOWNLOAD_PORT, nameBytes);
			
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (metaHandler.ableToDownload()) {				
				byte[] fileContentBytes = Receiver.receiveMultiplePackets(downloadSocket, serverAddress, 
						Protocol.DOWNLOAD_PORT);
				DataActions.writeFileContentToDirectory(file, fileContentBytes);

				Instant end = Instant.now();
				Duration timeElapsed = Duration.between(start, end); 
				
				view.showMessage("Received " + fileName + " and saved it in the local file directory. "
						+ "It took " + timeElapsed.toMillis() + " milliseconds \n");
			} else {
				view.showMessage("Did not receive " + fileName + " , because it does not exist on the server.");
			}
			metaHandler.setAbleToDownload(true);
		} catch (IOException e) {
			view.showMessage("IO exception at download initiator: " + e.getMessage());
		}	
	}
}