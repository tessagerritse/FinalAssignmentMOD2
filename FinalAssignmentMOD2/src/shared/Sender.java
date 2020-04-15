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

	private static void sendMultipleFilePackets(DatagramSocket socket, InetAddress address, int port, byte[] fileContent) {
		List<byte[]> listOfPackets = PacketManager.makeListOfFilePackets(fileContent); 
		
		for (byte[] singlePacket : listOfPackets) {
			DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
			sendPacketWaitForAck(socket, packet);	
		}
	}

	private static void sendFilePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileContent) {
		byte[] singlePacket = PacketManager.makeSingleFilePacket(Protocol.EOF, fileContent);
		DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
		sendPacketWaitForAck(socket, packet);
	}
	
	private static void sendNamePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileName) throws IOException {
		DatagramPacket packet = new DatagramPacket(fileName, fileName.length, address, port);
		socket.send(packet);
	}
	
	private static void sendPacketWaitForAck(DatagramSocket socket, DatagramPacket packet) {	
		boolean acked = false;		
		while (!acked) {
			try {
				socket.send(packet);
				Receiver.receiveAck(socket);
				acked = true;
			} catch (IOException e) {
				acked = false;
			}
		}
	}
	
	public static void sendAck(DatagramSocket socket, InetAddress address, int port) throws IOException {
		byte[] ack = new byte[Protocol.ACK_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(ack, ack.length, address, port);
		socket.send(packet);
	}

	public static void sendFeedback(DatagramSocket metaSocket, InetAddress clientAddress, String feedback) throws IOException {
		byte[] feedbackBytes = feedback.getBytes();
		DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, clientAddress, Protocol.CLIENT_META_PORT);
		metaSocket.send(feedbackPacket);
	}
}
