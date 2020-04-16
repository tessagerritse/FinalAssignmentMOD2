package main.java.fileClient;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.DataActions;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

/**
 * Gets a list of files from the server when user commands.
 * 
 * @author tessa.gerritse
 *
 */
public class ListInitiator implements Runnable {

	private FileClientTUI view;
	private DatagramSocket listSocket;
	private InetAddress serverAddress;
	private MetaHandler metaHandler;

	public ListInitiator(FileClientTUI view, DatagramSocket listSocket, InetAddress serverAddress, 
			MetaHandler metaHandler) {
		this.view = view;
		this.listSocket = listSocket;
		this.serverAddress = serverAddress;
		this.metaHandler = metaHandler;
	}

	@Override
	/**
	 * Sends a request for a list of files on the server, receives the list, and displays it 
	 * or displays feedback if the list is empty
	 */
	public void run() {
		try {
			byte[] listCommand = DataActions.getBytesFromString(Protocol.LIST);
			Sender.sendCommand(listSocket, serverAddress, Protocol.LIST_PORT, listCommand);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (metaHandler.ableToList()) {
				byte[] listOfFilesBytes = Receiver.receiveMultiplePackets(listSocket, serverAddress, 
						Protocol.LIST_PORT);			
				String[] listOfFiles = DataActions.getStringArrayFromByteArray(listOfFilesBytes);

				for (String file : listOfFiles) {
					view.showMessage(file);
				}	
			} else {
				view.showMessage("Did not receive a list of files, because the file directory on the server is empty.");
			}
		} catch (IOException e) {
			view.showMessage("IO exception at list initiator: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			view.showMessage("ClassNotFound exception at list initiator: " + e.getMessage());
		}
	}
}