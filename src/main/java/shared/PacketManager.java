package main.java.shared;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

/**
 * A class with packet-related methods.
 * Build packets or unpack packets.
 * 
 * Some comments are left, where sequence numbers were implemented.
 * Currently, they are not used for an ARQ protocol.
 * 
 * @author tessa.gerritse
 *
 */
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

		int dataPointer = 0;
		int packetPointer = 0;

		//for (int i = 0; i < numberOfSeqNumLoops; i++) {
			//int seqNum = 0;

			//while (packetPointer <= Protocol.MAX_SEQNUM && filePointer < contentBytes.length) {
			
			while(dataPointer < contentBytes.length) {

				int dataLength = Math.min(Protocol.DATA_SIZE, contentBytes.length - dataPointer);				
				byte[] packet = new byte[Protocol.HEADER + dataLength];
				
				if (packetPointer == numberOfPackets - 1) {					
					packet[Protocol.INFO] = (byte) Protocol.EOF;
				} else {
					packet[Protocol.INFO] = (byte) Protocol.NOT_EOF;
				}	
				//packet[Protocol.SEQNUM] = (byte) seqNum;
				
				byte[] packetData = DataActions.getDataByteArray(contentBytes, dataPointer, dataLength);
				packet[Protocol.LRC] = DataActions.calculateLRC(packetData);
				DataActions.addDataToPacket(packet, packetData);

				listOfPackets.add(packet);

				//seqNum++;
				packetPointer++;
				dataPointer += dataLength;
			}	
		//}		
		return listOfPackets;
	}

	public static byte[] unpackNameOrFeedbackPacket(DatagramPacket packet) {
		byte[] contentBytes = new byte[DataActions.getDataLength(packet)];
		byte[] packetData = DataActions.getData(packet);
		System.arraycopy(packetData, 0, contentBytes, 0, contentBytes.length);		
		return contentBytes;
	}

	public static int unpackPacketInfo(DatagramPacket packet) {
		byte infoByte = DataActions.getData(packet)[Protocol.INFO];
		return DataActions.getIntFromByte(infoByte);
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
