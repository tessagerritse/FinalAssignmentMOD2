package fileServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.Protocol;

public class UploadHandler implements Runnable {

	private DatagramSocket uploadSocket;
	private DatagramSocket metaSocket;
	private File fileDirectory;
	private int clientMetaPort;

	public UploadHandler(DatagramSocket uploadSocket, DatagramSocket metaSocket, File fileDirectory, int clientMetaPort) {
		this.uploadSocket = uploadSocket;
		this.metaSocket = metaSocket;
		this.fileDirectory = fileDirectory;
		this.clientMetaPort = clientMetaPort;
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] fileBytes= new byte[Protocol.FILE_PACKET_SIZE];
				DatagramPacket filePacket = new DatagramPacket(fileBytes, fileBytes.length);
				uploadSocket.receive(filePacket);

				byte[] fileNameBytes = new byte[Protocol.NAME_PACKET_SIZE];
				System.arraycopy(filePacket.getData(), 0, fileNameBytes, 0, fileNameBytes.length);
				String fileName = new String(fileNameBytes).trim();
				byte[] fileContentBytes = new byte[filePacket.getData().length - Protocol.NAME_PACKET_SIZE];
				System.arraycopy(filePacket.getData(), fileNameBytes.length, fileContentBytes, 0, fileContentBytes.length);

				File file = new File(fileDirectory + "/" + fileName);

				String feedback;
				if (!file.exists()) {
					feedback = "File " + fileName + " is saved on the server. \n";
				} else {
					feedback = "File " + fileName + " is overwritten on the server. \n";
				}

				OutputStream outputStream = new FileOutputStream(file);
				outputStream.write(fileContentBytes);
				outputStream.flush();
				outputStream.close();

				byte[] feedbackBytes = feedback.getBytes();
				DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, filePacket.getAddress(), clientMetaPort);
//				metaSocket.send(feedbackPacket);

				//TODO deze print verwijderen en in plaats daarvan meta werkend krijgen
				System.out.println(feedback);
			} catch (IOException e) {
				System.out.println("IO exception at upload handler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
