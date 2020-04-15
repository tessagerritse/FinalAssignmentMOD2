package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {

	public static byte[] receiveName(DatagramSocket socket, InetAddress address, int port) throws IOException {
		byte[] namePacket = new byte[Protocol.NAME_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(namePacket, namePacket.length);
		socket.receive(packet);
		return PacketManager.unpackNamePacket(packet);
	}

	public static byte[] receiveFile(DatagramSocket socket, InetAddress address, int port) throws IOException {		
		boolean lastPacket = false;
		byte[] fileContentBytes = null;
		
		while(!lastPacket) {
			DatagramPacket packet = receiveSingleFilePacket(socket, address, port);
			
			byte[] dataToAdd = PacketManager.unpackFilePacketData(packet);	
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

	private static DatagramPacket receiveSingleFilePacket(DatagramSocket socket, InetAddress address, int port) throws IOException {
		byte[] singleFilePacket = new byte[Protocol.PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(singleFilePacket, singleFilePacket.length);
		receivePacketSendAck(socket, packet, address, port);		
		return packet;
	}
	
	private static void receivePacketSendAck(DatagramSocket socket, DatagramPacket packet, InetAddress address, int port) throws IOException {
		socket.receive(packet);
		Sender.sendAck(socket, address, port);
	}

	public static void receiveAck(DatagramSocket socket) throws IOException {
		byte[] ack = new byte[Protocol.ACK_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(ack, ack.length);
		socket.receive(packet);
	}
}
