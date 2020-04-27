package shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;

/**
 * A class of various methods to use for file- and data management.
 * 
 * @author tessa.gerritse
 *
 */
public class Utils {

	public static File getFileObject(File fileDirectory, String fileName) {
		return new File(fileDirectory + "/" + fileName);
	}
	
	public static byte[] getFileContent(File directory) throws IOException {
		InputStream inputStream = new FileInputStream(directory);
		byte[] fileContentBytes = inputStream.readAllBytes();
		inputStream.close();
		return fileContentBytes;
	}
	
	public static void writeFileContentToDirectory(File directory, byte[] fileContentBytes) 
			throws IOException {
		OutputStream outputStream = new FileOutputStream(directory);
		outputStream.write(fileContentBytes);
		outputStream.flush();
		outputStream.close();
	}

	public static String getStringFromBytes(byte[] array) {
		return new String(array).trim();
	}

	public static int getIntFromByte(byte b) {
		return (int) b & 0xFF;
	}

	public static String[] getStringArrayFromByteArray(byte[] listOfFilesBytes) 
			throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(listOfFilesBytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		String[] listOfFiles = (String[]) objectInputStream.readObject();
		objectInputStream.close();
		return listOfFiles;
	}

	public static byte[] getByteArrayFromStringArray(String[] completeList) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(completeList);
		objectOutputStream.flush();
		objectOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}	

	/**
	 * Gets the length of the actual data in a packet. 
	 * Any empty parts of the packets will be trimmed.
	 * @param packet the packet you want to get the actual data length from
	 * @return data length (int)
	 */
	public static int getDataLength(DatagramPacket packet) {
		return packet.getLength();
	}

	/**
	 * Get the data from the packet. This data possibly includes header-bytes
	 * @param packet the packet you want to get the actual data from
	 * @return data array (byte)
	 */
	public static byte[] getData(DatagramPacket packet) {
		return packet.getData();
	}
	
	public static boolean fitsOnePacket(int contentLength) {
		return contentLength <= Protocol.DATA_SIZE;
	}

	public static byte[] combine2ByteArrays(byte[] firstArray, byte[] secondArray) {
		byte[] resultArray = new byte[firstArray.length + secondArray.length];
		System.arraycopy(firstArray, 0, resultArray, 0, firstArray.length);
		System.arraycopy(secondArray, 0, resultArray, firstArray.length, secondArray.length);
		return resultArray;
	}
	
	public static String[] combine2StringArrays(String[] firstArray, String[] secondArray) {
		String[] resultArray = new String[firstArray.length + secondArray.length];
		System.arraycopy(firstArray, 0, resultArray, 0, firstArray.length);
		System.arraycopy(secondArray, 0, resultArray, firstArray.length, secondArray.length);
		return resultArray;
	}	
	
	/**
	 * Gets a certain part of a large data array to put in a packet.
	 * @param contentBytes is the original large data array
	 * @param filePointer is the starting point for the new sub array
	 * @param dataLength is the end point for the new sub array
	 * @return sub array
	 */
	public static byte[] getDataByteArray(byte[] contentBytes, int filePointer, int dataLength) {
		byte[] dataByteArray = new byte[dataLength];
		System.arraycopy(contentBytes, filePointer, dataByteArray, 0, dataByteArray.length);
		return dataByteArray;
	}
	
	/**
	 * Puts data in a packet.
	 * @param packet  is the packet to add the data to
	 * @param packetData is the data to put in the packet
	 */
	public static void addDataToPacket(byte[] packet, byte[] packetData) {
		System.arraycopy(packetData, 0, packet, Protocol.HEADER, packetData.length);
	}
	
	/**
	 * LRC: Longitudinal redundancy check for integrity.
	 * @param contentBytes is the byte array of content data that is received in a packet
	 * @return LRC 
	 */
	public static byte calculateLRC(byte[] contentBytes) {
		byte LRC = 0;
		for (byte contentByte : contentBytes) {
			LRC ^= contentByte;
		}
		return LRC;
	}
}
