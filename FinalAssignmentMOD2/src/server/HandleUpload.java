package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class HandleUpload {

	private DatagramSocket uploadSocket;
	private File fileDirectory;

	public HandleUpload(DatagramSocket uploadSocket, File fileDirectory) {
		this.uploadSocket = uploadSocket;
		this.fileDirectory = fileDirectory;
	}

	public void start() {
		try {
			byte[] buffIn = new byte[26000];
			DatagramPacket uploadRequest = new DatagramPacket(buffIn, buffIn.length);
			uploadSocket.receive(uploadRequest);
			
			byte[] fileNameBytes = new byte[FileServer.MAX_NAME_LENGTH];
			System.arraycopy(buffIn, 0, fileNameBytes, 0, fileNameBytes.length);
			String fileName = new String(fileNameBytes).trim();
			byte[] fileContentBytes = new byte[buffIn.length - FileServer.MAX_NAME_LENGTH];
			System.arraycopy(buffIn, fileNameBytes.length, fileContentBytes, 0, fileContentBytes.length);
			
			File file = new File(fileDirectory + "/" + fileName);
			
			String newFile = "File " + fileName + " is a new file and has just been uploaded and saved. \n";
			String replaceFile = "File " + fileName + " has just been uploaded and saved. \n"
					+ "A file with the same name already existed, so it has been overwritten. \n";
			String confirmation = (file.createNewFile()) ? newFile : replaceFile;

			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(fileContentBytes);
			outputStream.flush();
			outputStream.close();
			
			System.out.println(confirmation);
		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		} 		
	}
}
