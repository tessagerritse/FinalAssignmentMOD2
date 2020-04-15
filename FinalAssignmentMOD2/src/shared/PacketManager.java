package shared;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class PacketManager {

	public static byte[] makeSinglePacket(int protocolInfo, byte[] contentBytes) {
		byte[] singlePacket = new byte[Protocol.HEADER + contentBytes.length];
		singlePacket[Protocol.INFO] = (byte) protocolInfo;
		singlePacket[Protocol.LRC] = DataActions.calculateLRC(contentBytes);
		System.arraycopy(contentBytes, 0, singlePacket, Protocol.HEADER, contentBytes.length);
		return singlePacket;
	}

	public static List<byte[]> makeMultiplePackets(byte[] contentBytes) {
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
				
				byte[] packetData = DataActions.getDataByteArray(contentBytes, filePointer, dataLength);
				packet[Protocol.LRC] = DataActions.calculateLRC(packetData);
				packet = DataActions.addDataToPacket(packet, packetData);				

				listOfPackets.add(packet);

				//seqNum++;
				packetPointer++;
				filePointer += dataLength;
			}	
		//}		
		return listOfPackets;
	}

	public static byte[] unpackNameOrFeedbackPacket(DatagramPacket packet) {
		byte[] fileNameBytes = new byte[DataActions.getDataLength(packet)];
		byte[] packetBytes = DataActions.getData(packet);
		System.arraycopy(packetBytes, 0, fileNameBytes, 0, fileNameBytes.length);		
		return fileNameBytes;
	}

	public static int unpackPacketInfo(DatagramPacket packet) {
		byte infoByte = DataActions.getData(packet)[Protocol.INFO];
		return DataActions.fromByteToInt(infoByte);
	}

	public static byte unpackPacketLRC(DatagramPacket packet) {
		return DataActions.getData(packet)[Protocol.LRC];
	}

	public static byte[] unpackPacketData(DatagramPacket packet) {
		byte[] data = new byte[DataActions.getDataLength(packet) - Protocol.HEADER];
		byte[] packetBytes = DataActions.getData(packet);
		System.arraycopy(packetBytes, Protocol.HEADER, data, 0, data.length);
		return data;
	}
}
