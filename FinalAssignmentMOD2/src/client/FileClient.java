package client;

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

	private FileClientTUI view;
	private DatagramSocket clientSocket;
	private InetAddress serverAddress;
	
	private int communicationPort;
	private int uploadPort;
	private int downloadPort;
	private int listPort;
	
	private UploadHandler uploadHandler;
	private DownloadHandler downloadHandler;
	private ListHandler listHander;
	
	public FileClient() {
		view = new FileClientTUI(this);
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
			getPortNumbers();
			view.start();
		} catch (UnknownHostException e) {
			view.showMessage(e.getMessage());
		} catch (SocketException e) {
			view.showMessage(e.getMessage());
		} catch (IOException e) {
			view.showMessage(e.getMessage());
		}
	}

	private void getPortNumbers() throws IOException {
		byte[] buffer0 = new byte[2];
		DatagramPacket setupResponse0 = new DatagramPacket(buffer0, buffer0.length);
		clientSocket.receive(setupResponse0);
		uploadPort = new BigInteger(buffer0).intValue();
		System.out.println("I get uploadPort: " + uploadPort);
		
		byte[] buffer1 = new byte[2];
		DatagramPacket setupResponse1 = new DatagramPacket(buffer1, buffer1.length);
		clientSocket.receive(setupResponse1);
		downloadPort = new BigInteger(buffer1).intValue();
		
		byte[] buffer2 = new byte[2];
		DatagramPacket setupResponse2 = new DatagramPacket(buffer2, buffer2.length);
		clientSocket.receive(setupResponse2);
		listPort = new BigInteger(buffer2).intValue();
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
		System.out.println("The uploadPort is :" + uploadPort);

		uploadHandler = new UploadHandler(view, clientSocket, serverAddress, uploadPort, fileName);
		new Thread(uploadHandler).start();		
	}

	private void downloadFile(String fileName) {
		// TODO Auto-generated method stub
		
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
		throw new ExitProgram("You have ordered to quit the program. Bye!");
	}
}