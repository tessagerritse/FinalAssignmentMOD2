package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import server.FileServer;

public class DownloadHandler implements Runnable {

	private ClientTUI view;
	private DatagramSocket clientSocket;
	private InetAddress serverAddress;
	private int downloadPort;
	private File fileDirectory;
	private int maxNameLength;
	private String fileName;

	public DownloadHandler(ClientTUI view, DatagramSocket clientSocket, InetAddress serverAddress, int downloadPort, File fileDirectory, int maxNameLength, String fileName) {
		this.view = view;
		this.clientSocket = clientSocket;
		this.serverAddress = serverAddress;
		this.downloadPort = downloadPort;
		this.fileDirectory = fileDirectory;
		this.maxNameLength = maxNameLength;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			byte[] fileNameBytes = fileName.getBytes();
			DatagramPacket downloadRequest = new DatagramPacket(fileNameBytes, fileNameBytes.length, serverAddress, downloadPort);
			clientSocket.send(downloadRequest);

			byte[] fileContentBytes = new byte[26000];
			DatagramPacket downloadFile = new DatagramPacket(fileContentBytes, fileContentBytes.length);
			clientSocket.receive(downloadFile);

			File file = new File(fileDirectory + "/" + fileName);

			String newFile = "File " + fileName + " is a new file and has just been downloaded and saved. \n";
			String replaceFile = "File " + fileName + " has just been downloaded and saved. \n"
					+ "A file with the same name already existed, so it has been overwritten. \n";
			String confirmation = (file.createNewFile()) ? newFile : replaceFile;

			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(fileContentBytes);
			outputStream.flush();
			outputStream.close();
			view.showMessage(confirmation);
		} catch (IOException e) {
			view.showMessage("IO error: " + e.getMessage());
		}		
	}
}
