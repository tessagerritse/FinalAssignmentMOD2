package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class with several methods to send several kinds of packets and/or multiple packets.
 * 
 * @author tessa.gerritse
 *
 */
public class Sender {

	public static void sendPacketWaitForAck(DatagramSocket socket, DatagramPacket packet) {
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

	public static void sendCommand(DatagramSocket socket, InetAddress address, int port, byte[] command) {
		DatagramPacket commandPacket = new DatagramPacket(command, command.length, address, port);
		sendPacketWaitForAck(socket, commandPacket);
	}
	
	public static void sendNamePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileName) {
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
