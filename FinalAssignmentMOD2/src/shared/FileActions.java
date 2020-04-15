package shared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileActions {

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

	public static byte[] addToByteArray(byte[] arrayToAddTo, byte[] dataToAdd) {
		byte[] resultArray = new byte[arrayToAddTo.length + dataToAdd.length];
		System.arraycopy(arrayToAddTo, 0, resultArray, 0, arrayToAddTo.length);
		System.arraycopy(dataToAdd, 0, resultArray, arrayToAddTo.length, dataToAdd.length);
		return resultArray;
	}
	
	public static byte calculateLRC(byte[] contentBytes) {
		byte LRC = 0;
		for (int i = 0; i < contentBytes.length; i++) {
			LRC ^= contentBytes[i];
		}
		return LRC;
	}
}
