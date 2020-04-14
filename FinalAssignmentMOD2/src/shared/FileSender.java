package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileSender {
	
	public static void sendFile(DatagramSocket socket, InetAddress address, int port, byte[] fileName, byte[] fileContent) throws IOException {
		if (FileActions.fitsOnePacket(fileName.length + fileContent.length)) {
			byte[] combinedArray = FileActions.combineNameAndFileArrays(fileName, fileContent);
			sendFilePacket(socket, address, port, combinedArray );
		} else if (FileActions.fitsOnePacket(fileContent.length)) {
			sendNamePacket(socket, address, port, fileName);
			sendFilePacket(socket, address, port, fileContent);
		} else {
			sendMultipleFilePackets(socket, address, port, fileName, fileContent);
		}
	}

	private static void sendMultipleFilePackets(DatagramSocket socket, InetAddress address, int port, byte[] fileName,
			byte[] fileContent) {
		
	}

	private static void sendNamePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileName) throws IOException {
		DatagramPacket filePacket = new DatagramPacket(fileName, Protocol.NAME_PACKET_SIZE, address, port);
		socket.send(filePacket);
	}

	private static void sendFilePacket(DatagramSocket socket, InetAddress address, int port, byte[] fileContent) throws IOException {
		DatagramPacket filePacket = new DatagramPacket(fileContent, fileContent.length, address, port);
		socket.send(filePacket);
	}

}
