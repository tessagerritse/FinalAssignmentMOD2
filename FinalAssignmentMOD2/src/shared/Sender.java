package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Sender {

	public static void sendFileInclName(DatagramSocket socket, InetAddress address, int port, byte[] fileName, byte[] fileContent) throws IOException {
		if (FileActions.fitsOnePacket(fileContent.length)) {
			sendNamePacket(socket, address, port, fileName);
			sendFilePacket(socket, address, port, fileContent);
		} else {
			sendNamePacket(socket, address, port, fileName);
			sendMultipleFilePackets(socket, address, port, fileContent);
		}
	}

	private static void sendMultipleFilePackets(DatagramSocket socket, InetAddress address, int port, byte[] fileContent) throws IOException {
		List<byte[]> listOfPackets = PacketManager.makeListOfFilePackets(fileContent); 
		for (byte[] singlePacket : listOfPackets) {
			DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
			socket.send(packet);
		}
	}

	private static void sendFilePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileContent) throws IOException {
		byte[] singlePacket = PacketManager.makeSingleFilePacket(Protocol.EOF, 0, fileContent);
		DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
		socket.send(packet);
	}
	
	private static void sendNamePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileName) throws IOException {
		DatagramPacket packet = new DatagramPacket(fileName, fileName.length, address, port);
		socket.send(packet);
	}

	public static void sendFeedback(DatagramSocket metaSocket, InetAddress clientAddress, String feedback) throws IOException {
		byte[] feedbackBytes = feedback.getBytes();
		DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, clientAddress, Protocol.CLIENT_META_PORT);
		metaSocket.send(feedbackPacket);
	}
}