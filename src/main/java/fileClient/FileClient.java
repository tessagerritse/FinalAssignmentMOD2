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

/**
 * Regulates the initiation of processes on command of the user. 
 * A meta-channel is started for the sole purpose of receiving 
 * feedback from the server to display to the user.
 * 
 * @author tessa.gerritse
 *
 */
public class FileClient {

	private final File fileDirectory;
	private final FileClientTUI view;
	private InetAddress serverAddress;

	private DatagramSocket metaSocket;
	private DatagramSocket uploadSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket removeSocket;
	private DatagramSocket listSocket;

	private MetaHandler metaHandler;

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
			startReceivingMeta();
			view.start();
		} catch (UnknownHostException e) {
			view.showMessage("Unknown host exception at getting server address: " + e.getMessage());
		} catch (SocketException e) {
			view.showMessage("Socket exception at creating sockets: " + e.getMessage());
		} catch (IOException e) {
			view.showMessage("IO exception at connecting to server: " + e.getMessage());
		}
	}

	private void startReceivingMeta() {
		metaHandler = new MetaHandler(view, metaSocket);
		new Thread(metaHandler).start();
	}

	private void connectToServer() throws IOException {	
		DatagramPacket connectRequest = new DatagramPacket(new byte[1], 1, serverAddress, Protocol.META_PORT);
		metaSocket.send(connectRequest);
		view.showMessage("Trying to connect to the server \n");

		byte[] feedbackBytes = new byte[Protocol.FEEDBACK_PACKET_SIZE];
		DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length);
		metaSocket.receive(feedbackPacket);
		String feedback = new String(feedbackPacket.getData()).trim();
		view.showMessage("Message from server: " + feedback + "\n");

		serverAddress = feedbackPacket.getAddress();
	}

	private void setupSockets() throws SocketException {
		metaSocket = new DatagramSocket(Protocol.CLIENT_META_PORT);
		uploadSocket = new DatagramSocket(Protocol.CLIENT_UPLOAD_PORT);
		downloadSocket = new DatagramSocket(Protocol.CLIENT_DOWNLOAD_PORT);
		removeSocket = new DatagramSocket(Protocol.CLIENT_REMOVE_PORT);
		listSocket = new DatagramSocket(Protocol.CLIENT_LIST_PORT);
	}

	private void setupDirectory() {
		if (!fileDirectory.exists()) {
			fileDirectory.mkdir();
			view.showMessage("The directory " + fileDirectory + " has just been created. "
					+ "Put files in that directory if you want to be able to upload them to the server.");
		} else {
			view.showMessage("Put files in " + fileDirectory + " if you want to be able to upload them to the server.");
		}
	}

	public void handleRequest(String command, String fileName) throws ExitProgram {				
		switch (command) {
		case Protocol.UPLOAD:	
			UploadInitiator uploadInitiator = new UploadInitiator(view, uploadSocket, serverAddress, 
					fileDirectory, fileName);
			new Thread(uploadInitiator).start();
			break;
		case Protocol.DOWNLOAD:
			DownloadInitiator downloadInitiator = new DownloadInitiator(view, downloadSocket, serverAddress, 
					fileDirectory, fileName, metaHandler);
			new Thread(downloadInitiator).start();
			break;
		case Protocol.REMOVE:
			RemoveInitiator removeInitiator = new RemoveInitiator(view, removeSocket, serverAddress, 
					fileName);
			new Thread(removeInitiator).start();
			break;
		case Protocol.LIST:
			ListInitiator listInitiator = new ListInitiator(view, listSocket, serverAddress, metaHandler);
			new Thread(listInitiator).start();
			break;
		case Protocol.QUIT:
			quitProgram();
			break;
		}
	}

	private void quitProgram() throws ExitProgram {
		view.showMessage("You have ordered to quit the program. Bye!");
		metaHandler.setListen(false);
		uploadSocket.close();
		downloadSocket.close();
		removeSocket.close();
		listSocket.close();
		metaSocket.close();
		throw new ExitProgram("User has ordered to quit the program. Client is closed.");
	}
}
