package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import shared.Protocol;

public class DownloadHandler implements Runnable {

	private DatagramSocket downloadSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private int clientMetaPort;

	public DownloadHandler(DatagramSocket downloadSocket, DatagramSocket metaSocket, File fileDirectory, int clientMetaPort) {
		this.downloadSocket = downloadSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientMetaPort = clientMetaPort;
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] nameBytes= new byte[Protocol.NAME_PACKET_SIZE];
				DatagramPacket namePacket = new DatagramPacket(nameBytes, nameBytes.length);
				downloadSocket.receive(namePacket);

				String fileName = new String(namePacket.getData()).trim();

				File file = new File(fileDirectory + "/" + fileName);

				String feedback;
				if (!file.exists()) {
					file.delete();
					feedback = "File " + fileName + " doesn't exist on server. \n";
				} else {
					Path path = Paths.get(file.toURI());			
					byte[] fileContentBytes = Files.readAllBytes(path);
					DatagramPacket downloadResponse = new DatagramPacket(fileContentBytes, fileContentBytes.length, namePacket.getAddress(), namePacket.getPort());
					downloadSocket.send(downloadResponse);
					feedback = "Sent file " + fileName + " from server. \n";
				}
				byte[] feedbackBytes = feedback.getBytes();
				DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, namePacket.getAddress(), clientMetaPort);
				metaSocket.send(feedbackPacket);
			} catch (IOException e) {
				System.out.println("IO exception at download handler: " + e.getMessage());
			}
		}
	}	
}
