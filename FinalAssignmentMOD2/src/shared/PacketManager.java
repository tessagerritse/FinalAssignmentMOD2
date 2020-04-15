package shared;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class PacketManager {

	public static byte[] makeSingleFilePacket(int protocolInfo, byte[] contentBytes) {
		byte[] singlePacket = new byte[Protocol.HEADER + contentBytes.length];
		singlePacket[Protocol.INFO] = (byte) protocolInfo;
		singlePacket[Protocol.LRC] = FileActions.calculateLRC(contentBytes);
		System.arraycopy(contentBytes, 0, singlePacket, Protocol.HEADER, contentBytes.length);
		return singlePacket;
	}

	public static List<byte[]> makeListOfFilePackets(byte[] contentBytes) {
		List<byte[]> listOfPackets = new ArrayList<>();

		int numberOfPackets = (int) Math.ceil((double)contentBytes.length/(double)Protocol.DATA_SIZE);		
		//int numberOfSeqNumLoops = (int) Math.ceil((double)numberOfPackets/(double)Protocol.MAX_SEQNUM);

		int filePointer = 0;
		int packetPointer = 0;

		//for (int i = 0; i < numberOfSeqNumLoops; i++) {
			//int seqNum = 0;

			//while (packetPointer <= Protocol.MAX_SEQNUM && filePointer < contentBytes.length) {
			
			while(filePointer < contentBytes.length) {

				int dataLength = Math.min(Protocol.DATA_SIZE, contentBytes.length - filePointer);				
				byte[] packet = new byte[Protocol.HEADER + dataLength];
				
				if (packetPointer == numberOfPackets - 1) {					
					packet[Protocol.INFO] = (byte) Protocol.EOF;
				} else {
					packet[Protocol.INFO] = (byte) Protocol.NOT_EOF;
				}	
				//packet[Protocol.SEQNUM] = (byte) seqNum;
				
				byte[] packetData = FileActions.getDataByteArray(contentBytes, filePointer, dataLength);
				packet[Protocol.LRC] = FileActions.calculateLRC(packetData);
				packet = FileActions.addDataToPacket(packet, packetData);				

				listOfPackets.add(packet);

				//seqNum++;
				packetPointer++;
				filePointer += dataLength;
			}	
		//}		
		return listOfPackets;
	}

	public static byte[] unpackNamePacket(DatagramPacket packet) {
		byte[] fileNameBytes = new byte[FileActions.getDataLength(packet)];
		byte[] packetBytes = FileActions.getData(packet);
		System.arraycopy(packetBytes, 0, fileNameBytes, 0, fileNameBytes.length);		
		return fileNameBytes;
	}

	public static int unpackFilePacketInfo(DatagramPacket packet) {
		byte infoByte = FileActions.getData(packet)[Protocol.INFO];
		return FileActions.fromByteToInt(infoByte);
	}

	public static byte unpackFilePacketLRC(DatagramPacket packet) {
		return FileActions.getData(packet)[Protocol.LRC];
	}

	public static byte[] unpackFilePacketData(DatagramPacket packet) {
		byte[] data = new byte[FileActions.getDataLength(packet) - Protocol.HEADER];
		byte[] packetBytes = FileActions.getData(packet);
		System.arraycopy(packetBytes, Protocol.HEADER, data, 0, data.length);
		return data;
	}
}
