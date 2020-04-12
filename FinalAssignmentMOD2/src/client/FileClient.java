package client;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import exceptions.ExitProgram;
import transmission.ProtocolMessages;

public class FileClient {

	private File fileDirectory;
	private ClientTUI view;
	private DatagramSocket clientSocket;
	private InetAddress serverAddress;
	
	private int communicationPort;
	private int uploadPort;
	private int downloadPort;
	private int listPort;
	private int maxNameLength;
	
	public FileClient() {
		fileDirectory = new File(System.getProperty("user.home") + "/FilesOnClient");
		view = new ClientTUI(this);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Syntax: FileClient <hostname> <port>");
			return;
		}

		String hostName = args[0];
		int port = Integer.parseInt(args[1]);
		
		(new FileClient()).start(hostName, port);
		
	}
	
	public void start(String hostName, int port) {
		try {
			serverAddress = InetAddress.getByName(hostName);
			communicationPort = port;
			
			clientSocket = new DatagramSocket();
			
			createConnection();		
			receiveMaxNameLengthAndPortNumbers();
			createFileDirectory();
			view.start(maxNameLength);
		} catch (UnknownHostException e) {
			view.showMessage(e.getMessage());
		} catch (SocketException e) {
			view.showMessage(e.getMessage());
		} catch (IOException e) {
			view.showMessage(e.getMessage());
		}
	}

	private void createFileDirectory() {
		if (!fileDirectory.exists()) {
			fileDirectory.mkdir();
	    }
		view.showMessage("You are now connected to the server.");
		view.showMessage("Files will be uploaded from and/or saved in " + fileDirectory + ". \n");
	}

	private void receiveMaxNameLengthAndPortNumbers() throws IOException {
		byte[] buffIn = new byte[7];
		DatagramPacket getPortNumbersAndMaxNameLength = new DatagramPacket(buffIn, buffIn.length);
		clientSocket.receive(getPortNumbersAndMaxNameLength);
				
		byte[] maxNameBytes = new byte[1];
		System.arraycopy(buffIn, 0, maxNameBytes, 0, maxNameBytes.length);
		maxNameLength = new BigInteger(maxNameBytes).intValue();
		
		byte[] uploadPortBytes = new byte[2];
		System.arraycopy(buffIn, maxNameBytes.length, uploadPortBytes, 0, uploadPortBytes.length);
		uploadPort = new BigInteger(uploadPortBytes).intValue();
		
		byte[] downloadPortBytes = new byte[2];
		System.arraycopy(buffIn, maxNameBytes.length + uploadPortBytes.length, downloadPortBytes, 
				0, downloadPortBytes.length);
		downloadPort = new BigInteger(downloadPortBytes).intValue();
		
		byte[] listPortBytes = new byte[2];
		System.arraycopy(buffIn, maxNameBytes.length + uploadPortBytes.length + 
				downloadPortBytes.length, listPortBytes, 0, listPortBytes.length);
		listPort = new BigInteger(listPortBytes).intValue();
	}	
	
	private void createConnection() throws IOException {
		DatagramPacket setupRequest = new DatagramPacket(new byte[1], 1, serverAddress, 
				communicationPort);
		clientSocket.send(setupRequest);
		view.showMessage("Connecting to the server \n");
	}

	public void handleRequest(String command, String fileName) throws ExitProgram, IOException {
		switch (command) {
		case ProtocolMessages.UPLOAD:
			uploadFile(fileName);
			break;
		case ProtocolMessages.DOWNLOAD:
			downloadFile(fileName);
			break;
		case ProtocolMessages.REMOVE:
			removeFile(fileName);
			break;
		case ProtocolMessages.LIST:
			getListOfFiles();
			break;
		case ProtocolMessages.QUIT:
			quitProgram();
			break;
		}
	}
	
	private void uploadFile(String fileName) throws IOException {
		UploadHandler uploadHandler = new UploadHandler(view, clientSocket, serverAddress, uploadPort, maxNameLength, fileName);
		new Thread(uploadHandler).start();		
	}

	private void downloadFile(String fileName) {
		DownloadHandler downloadHandler = new DownloadHandler(view, clientSocket, serverAddress, downloadPort, maxNameLength, fileName);
		new Thread(downloadHandler).start();
	}

	private void removeFile(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private void getListOfFiles() {
		// TODO Auto-generated method stub
		
	}

	public void quitProgram() throws ExitProgram {
		view.showMessage("You have ordered to quit the program. Bye!");
		clientSocket.close();
		throw new ExitProgram("User has ordered to quit the program. Client is closed.");
	}
}