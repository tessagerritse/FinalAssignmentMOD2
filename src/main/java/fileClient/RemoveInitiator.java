package main.java.fileClient;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import main.java.shared.DataActions;
import main.java.shared.Protocol;
import main.java.shared.Sender;

/**
 * Sends a remove request of a certain file when user commands.
 * 
 * @author tessa.gerritse
 *
 */
public class RemoveInitiator implements Runnable {

	private FileClientTUI view;
	private DatagramSocket removeSocket;
	private InetAddress serverAddress;
	private String fileName;

	public RemoveInitiator(FileClientTUI view, DatagramSocket removeSocket, InetAddress serverAddress,
			String fileName) {
		this.view = view;
		this.removeSocket = removeSocket;
		this.serverAddress = serverAddress;
		this.fileName = fileName;
	}

	@Override
	/**
	 * Sends name of the file that has to be removed, receives feedback if it was successful or not,
	 * and displays feedback
	 */
	public void run() {
		try {
			byte[] nameBytes = DataActions.getBytesFromString(fileName);
			Sender.sendNamePacket(removeSocket, serverAddress, Protocol.REMOVE_PORT, nameBytes);
			view.showMessage("Sent request to server to remove " + fileName + ". \n");
		} catch (IOException e) {
			view.showMessage("IO exception at remove initiator: " + e.getMessage());
		}
	}

}
