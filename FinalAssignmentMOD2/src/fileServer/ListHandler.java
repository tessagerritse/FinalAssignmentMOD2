package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.DataActions;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

public class ListHandler implements Runnable {

	private DatagramSocket listSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public ListHandler(DatagramSocket listSocket, DatagramSocket metaSocket, File fileDirectory, InetAddress clientAddress) {
		this.listSocket = listSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Receiver.receiveCommand(listSocket, clientAddress, Protocol.CLIENT_LIST_PORT);

				String[] listOfFiles = fileDirectory.list();
				
				String feedback;
				if (listOfFiles.length == 0) {
					feedback = "The file directory on the server is empty. \n";
				} else {
					String[] guidingMessage = {"There are " + listOfFiles.length + " files on the server: "};
					String[] completeList = DataActions.combine2StringArrays(guidingMessage, listOfFiles);
					
					for (String file : listOfFiles) {
						System.out.println(file);
					}
					
					byte[] completeListBytes = DataActions.getByteArrayFromStringArray(completeList);
					Sender.sendSingleOrMultiplePackets(listSocket, clientAddress, Protocol.CLIENT_LIST_PORT, completeListBytes);
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
