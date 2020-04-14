package shared;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileActions {

	public static File getFileObject(File fileDirectory, String fileName) {
		File file = new File(fileDirectory + "/" + fileName);
		return file;
	}

	public static boolean exists(File file) {
		return file.exists();
	}

	public static byte[] getStringBytes(String string) {
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

	public static byte[] combineNameAndFileArrays(byte[] firstArray, byte[] secondArray) {
		byte[] combinedArray = new byte[Protocol.NAME_PACKET_SIZE + secondArray.length];
		System.arraycopy(firstArray, 0, combinedArray, 0, firstArray.length);
		System.arraycopy(secondArray, 0, combinedArray, Protocol.NAME_PACKET_SIZE, secondArray.length);
		return combinedArray;
	}
	
}
