package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver {
	
	public static byte[] receiveName(DatagramSocket socket) throws IOException {
		byte[] namePacket = new byte[Protocol.NAME_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(namePacket, namePacket.length);
		socket.receive(packet);		
		return PacketManager.unpackNamePacket(packet);
	}

	public static byte[] receiveFile(DatagramSocket socket) throws IOException {		
		boolean lastPacket = false;
		byte[] fileContentBytes = null;

		while(!lastPacket) {
			DatagramPacket packet = receiveSingleFilePacket(socket);
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

	private static DatagramPacket receiveSingleFilePacket(DatagramSocket socket) throws IOException {
		byte[] singleFilePacket = new byte[Protocol.PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(singleFilePacket, singleFilePacket.length);
		socket.receive(packet);		
		return packet;
	}
}
