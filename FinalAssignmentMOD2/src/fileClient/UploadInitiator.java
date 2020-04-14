package fileClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import shared.Protocol;

public class UploadInitiator implements Runnable {

	private FileClientTUI view;
	private DatagramSocket uploadSocket;
	private InetAddress serverAddress;
	private File fileDirectory;
	private String fileName;
	
	public UploadInitiator(FileClientTUI view, DatagramSocket uploadSocket, InetAddress serverAddress,
			File fileDirectory, String fileName) {
		this.view = view;
		this.uploadSocket = uploadSocket;
		this.serverAddress = serverAddress;
		this.fileDirectory = fileDirectory;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			File file = new File(fileDirectory + "/" + fileName);

			String feedback;
			if (!file.exists()) {
				file.delete();
				feedback = "File " + fileName + " doesn't exist in the directory. Please try again. \n";
			} else {				
				Path path = Paths.get(file.toURI());
				byte[] fileNameBytes = fileName.getBytes();
				byte[] fileContentBytes = Files.readAllBytes(path);
				byte[] fileBytes = new byte[Protocol.NAME_PACKET_SIZE + fileContentBytes.length];
				System.arraycopy(fileNameBytes, 0, fileBytes, 0, fileNameBytes.length);
				System.arraycopy(fileContentBytes, 0, fileBytes, Protocol.NAME_PACKET_SIZE, fileContentBytes.length);	
				
				//new FileSender(fileBytes, serverAddress, uploadSocket).sendFile();
								
				DatagramPacket filePacket = new DatagramPacket(fileBytes, fileBytes.length, serverAddress, Protocol.UPLOAD_PORT);
				uploadSocket.send(filePacket);
				feedback = "File " + fileName + " is uploaded to the server. \n";
			}
			view.showMessage(feedback);	
		} catch (IOException e) {
			view.showMessage("IO exception at upload initiator " + e.getMessage());
		}	
	}
}
