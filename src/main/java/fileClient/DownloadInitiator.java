package main.java.fileClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

	private FileClientTUI view;
	private DatagramSocket downloadSocket;
	private InetAddress serverAddress;
	private File fileDirectory;
	private String fileName;
	private MetaHandler metaHandler;

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

			if (DataActions.exists(file)) {
				view.showMessage("File " + fileName + " already exists and will thus be overwritten. \n");
			}
			
			byte[] nameBytes = DataActions.getBytesFromString(fileName);
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
				view.showMessage("Received " + fileName + " and saved it in the local file directory.");
			} else {
				view.showMessage("Did not receive " + fileName + " , because it does not exist on the server.");
			}
			
		} catch (IOException e) {
			view.showMessage("IO exception at download initiator: " + e.getMessage());
		}	
	}
}