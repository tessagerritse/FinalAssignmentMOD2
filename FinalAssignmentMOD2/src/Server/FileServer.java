package Server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class FileServer {
	
	private DatagramSocket socket;
	private List<String> filesOnServer = new ArrayList<>();
	private String path = "/Users/tessa.gerritse/OneDrive - Nedap/Documents/University/MOD2 (MOD3 UT)/FinalAssignment/";
	
	public FileServer(int port) throws SocketException {
		socket = new DatagramSocket(port);
		
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Syntax: FileServer <port>");
			return;
		}
		
		int port = Integer.parseInt(args[0]);
		
		try {
			FileServer server = new FileServer(port);
			server.service();
		} catch (SocketException e) {
			System.out.println("Socket error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("I/O error: " + e.getMessage());
		}
	}

	private void service() throws IOException {
		while (true) {
			byte[] buffIn = new byte[25000];			
			DatagramPacket request = new DatagramPacket(buffIn, buffIn.length);
			socket.receive(request);
			
			String fileName = "File" + filesOnServer.size();
			File file = new File(path + fileName);
			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(request.getData());
			System.out.println("Writing data to file on server");
			outputStream.close();
			filesOnServer.add(fileName);
			
			String responseMessage = fileName + " is uploaded to the server";
			byte[] buffOut = responseMessage.getBytes();
			InetAddress clientAddress = request.getAddress();
			int clientPort = request.getPort();
			
			DatagramPacket response = new DatagramPacket(buffOut, buffOut.length, clientAddress, clientPort);
			socket.send(response);
			
		}
	}
}
