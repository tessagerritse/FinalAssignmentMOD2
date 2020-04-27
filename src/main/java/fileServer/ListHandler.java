package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import sender.MultiplePacketSender;
import sender.SinglePacketSender;
import shared.Utils;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

/**
 * Sends a list of files on the server to the client when the user requests it.
 * 
 * @author tessa.gerritse
 *
 */
public class ListHandler implements Runnable {

	private final DatagramSocket listSocket;
	private final DatagramSocket metaSocket;
	private final File fileDirectory;
	private final InetAddress clientAddress;

	private boolean listenForLists = true;

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
		while (listenForLists) {
			try {
				Receiver.receiveCommand(listSocket, clientAddress, Protocol.CLIENT_LIST_PORT);
				String[] listOfFiles = fileDirectory.list();

				if ((listOfFiles.length == 0)) {
					String feedback = "The file directory on the server is empty. \n";
					byte[] feedbackBytes = feedback.getBytes();
					Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
				} else {
					String[] guidingMessage = {"There are " + listOfFiles.length + " files on the server: "};
					String[] completeList = Utils.combine2StringArrays(guidingMessage, listOfFiles);
					
					byte[] completeListBytes = Utils.getByteArrayFromStringArray(completeList);
					if (completeListBytes.length <= Protocol.DATA_SIZE) {
						(new SinglePacketSender()).send(listSocket, clientAddress, Protocol.CLIENT_LIST_PORT,
								completeListBytes);
					} else {
						(new MultiplePacketSender()).send(listSocket, clientAddress, Protocol.CLIENT_LIST_PORT,
								completeListBytes);
					}

					String feedback = "Sent a list of files on server. \n";
					byte[] feedbackBytes = feedback.getBytes();
					Sender.sendFeedback(metaSocket, clientAddress, feedbackBytes);
				}
			} catch (IOException e) {
				setListenForLists(false);
				System.out.println("IO exception at list handler: " + e.getMessage());
			}
		}
	}
	public void setListenForLists(boolean listenForLists) {
		this.listenForLists = listenForLists;
	}
}
