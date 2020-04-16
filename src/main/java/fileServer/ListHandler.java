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
 * Sends a list of files on the server to the client when the user requests it.
 * 
 * @author tessa.gerritse
 *
 */
public class ListHandler implements Runnable {

	private DatagramSocket listSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public ListHandler(DatagramSocket listSocket, DatagramSocket metaSocket, File fileDirectory, 
			InetAddress clientAddress) {
		this.listSocket = listSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	/**
	 * Continually listens for a list-request and then sends the current list of files in the local directory 
	 * or feedback if the local directory is empty.
	 */
	public void run() {
		while (true) {
			try {
				Receiver.receiveCommand(listSocket, clientAddress, Protocol.CLIENT_LIST_PORT);

				String[] listOfFiles = DataActions.getListOfFiles(fileDirectory);
				
				String feedback;
				if (listOfFiles.length == 0) {
					feedback = "The file directory on the server is empty. \n";
				} else {
					String[] guidingMessage = {"There are " + listOfFiles.length + " files on the server: "};
					String[] completeList = DataActions.combine2StringArrays(guidingMessage, listOfFiles);
					
					byte[] completeListBytes = DataActions.getByteArrayFromStringArray(completeList);
					Sender.sendSingleOrMultiplePackets(listSocket, clientAddress, Protocol.CLIENT_LIST_PORT, 
							completeListBytes);
					feedback = "Sent a list of files on server. \n";
				}
				byte[] feedbackBytes = DataActions.getBytesFromString(feedback);
				Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
			} catch (IOException e) {
				System.out.println("IO exception at list handler: " + e.getMessage());
			}
		}
	}

}
