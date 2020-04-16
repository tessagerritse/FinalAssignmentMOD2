package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * Class with several methods to send several kinds of packets and/or multiple packets.
 * 
 * @author tessa.gerritse
 *
 */
public class Sender {

	/**
	 * Checks if the data to send fits in one packet and 
	 * executes a single packet or multiple packet -send accordingly.
	 * @param socket the socket from which to send the packet(s)
	 * @param address the address to send the packet(s) to
	 * @param port the port to send the packet(s) to
	 * @param content the content to send in the packet(s)
	 * @throws IOException when the socket cannot send for some reason
	 */
	public static void sendSingleOrMultiplePackets(DatagramSocket socket, InetAddress address, int port, 
			byte[] content) throws IOException {
		if (DataActions.fitsOnePacket(content.length)) {				
			sendSinglePacket(socket, address, port, content);
		} else {			
			sendMultiplePackets(socket, address, port, content);
		}
	}
	
	public static void sendSinglePacket(DatagramSocket socket, InetAddress address, int port, 
			byte[] content) {
		byte[] singlePacket = PacketManager.makeSinglePacket(Protocol.EOF, content);
		DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
		sendPacketWaitForAck(socket, packet);
	}

	private static void sendMultiplePackets(DatagramSocket socket, InetAddress address, int port, 
			byte[] content) {
		List<byte[]> listOfPackets = PacketManager.makeMultiplePackets(content); 
		
		for (byte[] singlePacket : listOfPackets) {
			DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
			sendPacketWaitForAck(socket, packet);	
		}
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
	
	public static void sendCommand(DatagramSocket socket, InetAddress address, int port, byte[] command) 
			throws IOException {
		DatagramPacket commandPacket = new DatagramPacket(command, command.length, address, port);
		sendPacketWaitForAck(socket, commandPacket);
	}
	
	public static void sendNamePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileName) 
			throws IOException {
		DatagramPacket namePacket = new DatagramPacket(fileName, fileName.length, address, port);
		sendPacketWaitForAck(socket, namePacket);
	}
	
	public static void sendFeedback(DatagramSocket metaSocket, InetAddress clientAddress, 
			byte[] feedbackBytes) throws IOException {
		DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length, 
				clientAddress, Protocol.CLIENT_META_PORT);
		metaSocket.send(feedbackPacket);
	}
	
	public static void sendAck(DatagramSocket socket, InetAddress address, int port) throws IOException {
		byte[] ack = new byte[Protocol.ACK_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(ack, ack.length, address, port);
		socket.send(packet);
	}	
}
