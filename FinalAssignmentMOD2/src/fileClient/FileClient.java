package fileClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import exceptions.ExitProgram;
import shared.Protocol;

public class FileClient {

	private File fileDirectory;
	private FileClientTUI view;
	private InetAddress serverAddress;
	
	private DatagramSocket metaSocket;
	private DatagramSocket uploadSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket removeSocket;
	private DatagramSocket listSocket;

	public FileClient() {
		fileDirectory = new File(System.getProperty("user.home") + "/FilesOnClient");
		view = new FileClientTUI(this);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Syntax: FileClient <hostname>");
			return;
		}

		String hostName = args[0];

		(new FileClient()).start(hostName);
	}

	public void start(String hostName) {
		try {
			serverAddress = InetAddress.getByName(hostName);
			setupDirectory();
			setupSockets();
			connectToServer();
			
			view.start();
		} catch (UnknownHostException e) {
			view.showMessage("Unknown host exception at getting server address: " + e.getMessage());
		} catch (SocketException e) {
			view.showMessage("Socket exception at creating client socket: " + e.getMessage());
		} catch (IOException e) {
			view.showMessage("IO exception at connecting to server: " + e.getMessage());
		}
	}

	private void startReceivingMeta() throws IOException {
		while (true) {
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			String feedback = receiveFeedback();
			view.showMessage("Message from server: " + feedback);
		}
	}

	private String receiveFeedback() throws IOException {
		byte[] feedbackBytes = new byte[Protocol.FEEDBACK_PACKET_SIZE];
		DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length);
		metaSocket.receive(feedbackPacket);
		String feedback = new String(feedbackPacket.getData()).trim();
		return feedback;
	}

	private void connectToServer() throws IOException {
		DatagramPacket connectRequest = new DatagramPacket(new byte[1], 1, serverAddress, Protocol.META_PORT);
		metaSocket.send(connectRequest);
		view.showMessage("Trying to connect to the server \n");
		
		String feedback = receiveFeedback();
		view.showMessage(feedback);
	}

	private void setupSockets() throws SocketException {
		metaSocket = new DatagramSocket();
		uploadSocket = new DatagramSocket();
		downloadSocket = new DatagramSocket();
		removeSocket = new DatagramSocket();
		listSocket = new DatagramSocket();
	}

	private void setupDirectory() {
		if (!fileDirectory.exists()) {
			fileDirectory.mkdir();
		}
	}

	public void handleRequest(String command, String fileName) throws ExitProgram {				
		switch (command) {
		case Protocol.UPLOAD:	
			UploadInitiator uploadInitiator = new UploadInitiator(view, uploadSocket, serverAddress, fileDirectory, fileName);
			new Thread(uploadInitiator).start();
			break;
		case Protocol.DOWNLOAD:
			DownloadInitiator downloadInitiator = new DownloadInitiator(view, downloadSocket, serverAddress, fileDirectory, fileName);
			new Thread(downloadInitiator).start();
			break;
		case Protocol.REMOVE:
			RemoveInitiator removeInitiator = new RemoveInitiator(view, removeSocket, serverAddress, fileName);
			new Thread(removeInitiator).start();
			break;
		case Protocol.LIST:
			ListInitiator listInitiator = new ListInitiator(view, listSocket, serverAddress);
			new Thread(listInitiator).start();
			break;
		case Protocol.QUIT:
			quitProgram();
			break;
		}
	}

	private void quitProgram() throws ExitProgram {
		view.showMessage("You have ordered to quit the program. Bye!");
		metaSocket.close();
		uploadSocket.close();
		downloadSocket.close();
		removeSocket.close();
		listSocket.close();
		throw new ExitProgram("User has ordered to quit the program. Client is closed.");
	}
}
