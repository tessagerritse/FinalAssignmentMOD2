package fileClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.FileActions;
import shared.Sender;
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
			File file = FileActions.getFileObject(fileDirectory, fileName);
			if (!FileActions.exists(file)) {
				view.showMessage("File " + fileName + " doesn't exist in the directory. Please try again. \n");
			} else {
				byte[] fileNameBytes = FileActions.getBytesFromString(fileName);
				byte[] fileContentBytes = FileActions.getFileContent(file);
				Sender.sendFileInclName(uploadSocket, serverAddress, Protocol.UPLOAD_PORT, fileNameBytes, fileContentBytes);
			}
			view.showMessage("File " + fileName + " is uploaded to the server. \n");	
		} catch (IOException e) {
			view.showMessage("IO exception at upload initiator " + e.getMessage());
		}	
	}
}
