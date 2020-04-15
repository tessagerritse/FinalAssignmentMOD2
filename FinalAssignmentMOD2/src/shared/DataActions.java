package shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataActions {

	public static File getFileObject(File fileDirectory, String fileName) {
		File file = new File(fileDirectory, fileName);
		return file;
	}

	public static boolean exists(File file) {
		return file.exists();
	}

	public static byte[] getBytesFromString(String string) {
		return string.getBytes();
	}

	public static byte[] getFileContent(File file) throws IOException {
		Path path = Paths.get(file.toURI());
		byte[] fileContentBytes = Files.readAllBytes(path);
		return fileContentBytes;
	}

	public static boolean fitsOnePacket(int contentLength) {
		return contentLength <= Protocol.DATA_SIZE;
	}

	public static String getStringFromBytes(byte[] array) {
		return new String(array).trim();
	}

	public static void writeFileContentToDirectory(File directory, byte[] fileContentBytes) throws IOException {
		OutputStream outputStream = new FileOutputStream(directory);
		outputStream.write(fileContentBytes);
		outputStream.flush();
		outputStream.close();
	}

	public static int getDataLength(DatagramPacket packet) {
		return packet.getLength();
	}

	public static byte[] getData(DatagramPacket packet) {
		return packet.getData();
	}

	public static int fromByteToInt(byte b) {
		return (int) b & 0xFF;
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
	
	public static byte[] getDataByteArray(byte[] contentBytes, int filePointer, int dataLength) {
		byte[] dataByteArray = new byte[dataLength];
		System.arraycopy(contentBytes, filePointer, dataByteArray, 0, dataByteArray.length);
		return dataByteArray;
	}
	
	public static byte calculateLRC(byte[] contentBytes) {
		byte LRC = 0;
		for (int i = 0; i < contentBytes.length; i++) {
			LRC ^= contentBytes[i];
		}
		return LRC;
	}

	public static byte[] addDataToPacket(byte[] packet, byte[] packetData) {
		System.arraycopy(packetData, 0, packet, Protocol.HEADER, packetData.length);
		return packet;
	}

	public static String[] getStringArrayFromByteArray(byte[] listOfFilesBytes) throws IOException, ClassNotFoundException {
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
}
