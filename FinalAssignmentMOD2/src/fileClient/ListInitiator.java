package fileClient;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.DataActions;
import shared.Protocol;
import shared.Receiver;
import shared.Sender;

public class ListInitiator implements Runnable {

	private FileClientTUI view;
	private DatagramSocket listSocket;
	private InetAddress serverAddress;

	public ListInitiator(FileClientTUI view, DatagramSocket listSocket, InetAddress serverAddress) {
		this.view = view;
		this.listSocket = listSocket;
		this.serverAddress = serverAddress;
	}

	@Override
	public void run() {
		try {
			byte[] listCommand = DataActions.getBytesFromString(Protocol.LIST);
			Sender.sendCommand(listSocket, serverAddress, Protocol.LIST_PORT, listCommand);
			
			byte[] listOfFilesBytes = Receiver.receiveMultiplePackets(listSocket, serverAddress, Protocol.CLIENT_LIST_PORT);
			String[] listOfFiles = DataActions.getStringArrayFromByteArray(listOfFilesBytes);
			
			for (String file : listOfFiles) {
				view.showMessage(file);
			}
		} catch (IOException e) {
			view.showMessage("IO exception at list initiator: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			view.showMessage("ClassNotFound exception at list initiator: " + e.getMessage());
		}
	}
}