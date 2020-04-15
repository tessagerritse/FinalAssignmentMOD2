package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {

	public static byte[] receiveName(DatagramSocket socket, InetAddress address) throws IOException {
		byte[] namePacket = new byte[Protocol.NAME_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(namePacket, namePacket.length);
		socket.receive(packet);
		return PacketManager.unpackNameOrFeedbackPacket(packet);
	}

	public static byte[] receiveFile(DatagramSocket socket, InetAddress address, int port) throws IOException {		
		boolean lastPacket = false;
		byte[] fileContentBytes = null;

		while(!lastPacket) {

			DatagramPacket packet = receiveSingleFilePacket(socket);

			byte LRC = PacketManager.unpackFilePacketLRC(packet);
			byte[] dataToAdd = PacketManager.unpackFilePacketData(packet);	

			if (LRC == FileActions.calculateLRC(dataToAdd)) {
				Sender.sendAck(socket, address, port);
			} 

			if (fileContentBytes == null) {
				fileContentBytes = dataToAdd;
			} else {		
				fileContentBytes = FileActions.addToByteArray(fileContentBytes, dataToAdd);
			}

			int info = PacketManager.unpackFilePacketInfo(packet);			
			if (info == Protocol.EOF) {
				lastPacket = true;
			}
		}
		return fileContentBytes;
	}

	private static DatagramPacket receiveSingleFilePacket(DatagramSocket socket) throws IOException {
		byte[] singleFilePacket = new byte[Protocol.PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(singleFilePacket, singleFilePacket.length);
		socket.receive(packet);		
		return packet;
	}

	public static void receiveAck(DatagramSocket socket) throws IOException {
		byte[] ack = new byte[Protocol.ACK_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(ack, ack.length);
		socket.setSoTimeout(1000);
		socket.receive(packet);
	}

	public static byte[] receiveFeedback(DatagramSocket metaSocket) throws IOException {
		byte[] feedbackBytes = new byte[Protocol.FEEDBACK_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(feedbackBytes, feedbackBytes.length);
		metaSocket.receive(packet);
		return PacketManager.unpackNameOrFeedbackPacket(packet);
	}
}
