package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class with several methods to receive several kinds of packets and/or multiple packets.
 * 
 * @author tessa.gerritse
 *
 */
public class Receiver {
	
	public static DatagramPacket receiveSinglePacket(DatagramSocket socket, InetAddress address, int port) 
			throws IOException {
		byte[] singleFilePacket = new byte[Protocol.PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(singleFilePacket, singleFilePacket.length);
		socket.receive(packet);	

		byte LRC = PacketManager.unpackPacketLRC(packet);
		byte[] data = PacketManager.unpackPacketData(packet);	

		if (LRC == DataActions.calculateLRC(data)) {
			Sender.sendAck(socket, address, port);
		} 
		
		return packet;
	}

	public static byte[] receiveMultiplePackets(DatagramSocket socket, InetAddress address, int port) 
			throws IOException {		
		boolean lastPacket = false;
		byte[] fileContentBytes = null;

		while(!lastPacket) {

			DatagramPacket packet = receiveSinglePacket(socket, address, port);	
			byte[] dataToAdd = PacketManager.unpackPacketData(packet);

			if (fileContentBytes == null) {
				fileContentBytes = dataToAdd;
			} else {		
				fileContentBytes = DataActions.combine2ByteArrays(fileContentBytes, dataToAdd);
			}

			int info = PacketManager.unpackPacketInfo(packet);			
			if (info == Protocol.EOF) {
				lastPacket = true;
			}
		}
		return fileContentBytes;
	}
	
	public static byte[] receiveName(DatagramSocket socket, InetAddress address, int port) 
			throws IOException {
		byte[] namePacket = new byte[Protocol.NAME_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(namePacket, namePacket.length);
		socket.receive(packet);
		Sender.sendAck(socket, address, port);
		return PacketManager.unpackNameOrFeedbackPacket(packet);
	}

	public static void receiveAck(DatagramSocket socket) throws IOException {
		byte[] ack = new byte[Protocol.ACK_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(ack, ack.length);
		
		socket.setSoTimeout(Protocol.TIMEOUT);
		socket.receive(packet);
		socket.setSoTimeout(0);

	}

	public static byte[] receiveFeedback(DatagramSocket metaSocket) throws IOException {
		byte[] feedbackBytes = new byte[Protocol.FEEDBACK_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(feedbackBytes, feedbackBytes.length);
		metaSocket.receive(packet);
		return PacketManager.unpackNameOrFeedbackPacket(packet);
	}

	public static void receiveCommand(DatagramSocket socket, InetAddress address, int port) 
			throws IOException {
		byte[] listCommand = new byte[Protocol.COMMAND_PACKET_SIZE];
		DatagramPacket listCommandPacket = new DatagramPacket(listCommand, listCommand.length);
		socket.receive(listCommandPacket);
		Sender.sendAck(socket, address, port);
	}
}
