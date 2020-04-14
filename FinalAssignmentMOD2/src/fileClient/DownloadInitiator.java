package fileClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.Protocol;

public class DownloadInitiator implements Runnable {

	private FileClientTUI view;
	private DatagramSocket downloadSocket;
	private InetAddress serverAddress;
	private File fileDirectory;
	private String fileName;

	public DownloadInitiator(FileClientTUI view, DatagramSocket downloadSocket, InetAddress serverAddress,
			File fileDirectory, String fileName) {
		this.view = view;
		this.downloadSocket = downloadSocket;
		this.serverAddress = serverAddress;
		this.fileDirectory = fileDirectory;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			byte[] nameBytes = fileName.getBytes();
			DatagramPacket namePacket = new DatagramPacket(nameBytes, nameBytes.length, serverAddress, Protocol.DOWNLOAD_PORT);
			downloadSocket.send(namePacket);

			// TODO: hier stoppen als file niet bestaat op server
			byte[] fileContentBytes = new byte[Protocol.PACKET_SIZE];
			DatagramPacket downloadFile = new DatagramPacket(fileContentBytes, fileContentBytes.length);
			downloadSocket.receive(downloadFile);

			File file = new File(fileDirectory + "/" + fileName);

			String feedback;
			if (file.exists()) {
				view.showMessage("File " + fileName + " already exists and will thus be overwritten. \n");
			}

			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(fileContentBytes);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			view.showMessage("IO exception at download initiator: " + e.getMessage());
		}	
	}

}
