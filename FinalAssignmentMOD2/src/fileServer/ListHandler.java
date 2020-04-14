package fileServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import shared.Protocol;

public class ListHandler implements Runnable {

	private DatagramSocket listSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private int clientMetaPort;

	public ListHandler(DatagramSocket listSocket, DatagramSocket metaSocket, File fileDirectory, int clientMetaPort) {
		this.listSocket = listSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientMetaPort = clientMetaPort;
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] listCommand = new byte[Protocol.COMMAND_PACKET_SIZE];
				DatagramPacket listCommandPacket = new DatagramPacket(listCommand, listCommand.length);
				listSocket.receive(listCommandPacket);

				String[] listOfFiles = fileDirectory.list();
				
				String feedback;
				if (listOfFiles.length == 0) {
					feedback = "There are to files on the server. \n";
				} else {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
					objectOutputStream.writeObject(listOfFiles);
					objectOutputStream.flush();
					objectOutputStream.close();

					byte[] listOfFilesBytes = byteArrayOutputStream.toByteArray();		
					DatagramPacket listResponse = new DatagramPacket(listOfFilesBytes, listOfFilesBytes.length, listCommandPacket.getAddress(), listCommandPacket.getPort());
					listSocket.send(listResponse);
					
					feedback = "Sent a list of files on server. \n";
				}
				byte[] feedbackBytes = feedback.getBytes();
				DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, listCommandPacket.getAddress(), clientMetaPort);
				metaSocket.send(feedbackPacket);
			} catch (IOException e) {
				System.out.println("IO exception at list handler: " + e.getMessage());
			}
		}
	}

}
