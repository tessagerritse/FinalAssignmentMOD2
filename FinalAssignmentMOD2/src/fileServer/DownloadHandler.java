package fileServer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import shared.Protocol;

public class DownloadHandler implements Runnable {

	private DatagramSocket downloadSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private InetAddress clientAddress;

	public DownloadHandler(DatagramSocket downloadSocket, DatagramSocket metaSocket, File fileDirectory, InetAddress clientAddress) {
		this.downloadSocket = downloadSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientAddress = clientAddress;
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
					DatagramPacket downloadResponse = new DatagramPacket(fileContentBytes, fileContentBytes.length, clientAddress, Protocol.CLIENT_DOWNLOAD_PORT);
					downloadSocket.send(downloadResponse);
					feedback = "Sent file " + fileName + " from server. \n";
				}
				byte[] feedbackBytes = feedback.getBytes();
				DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, namePacket.getAddress(), Protocol.CLIENT_META_PORT);
//				metaSocket.send(feedbackPacket);
				
				//TODO deze print verwijderen en meta werkend maken
				System.out.println(feedback);
				
			} catch (IOException e) {
				System.out.println("IO exception at download handler: " + e.getMessage());
			}
		}
	}	
}
