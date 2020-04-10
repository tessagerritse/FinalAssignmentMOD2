package X;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		
//		final BigInteger bi = BigInteger.valueOf(8888);
//		final byte[] bytes = bi.toByteArray();
//
//		System.out.println(Arrays.toString(bytes));
//
//		final int i = new BigInteger(bytes).intValue();
//		System.out.println(i);
		

		Path filePath = Paths.get("/Users/tessa.gerritse/git/FinalAssignmentMOD2/"
				+ "FinalAssignmentMOD2/src/client/tiny.pdf");
		
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			
			byte[] fileContent = Files.readAllBytes(filePath);
			System.out.println("read bytes from file");
			System.out.println("lengt of file byte array is " + fileContent.length);
			DatagramPacket sendFileContent = new DatagramPacket(fileContent, fileContent.length, 
					InetAddress.getByName("localhost"), 8888);
			clientSocket.send(sendFileContent);
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String savePath = "/Users/tessa.gerritse/OneDrive - "
				+ "Nedap/Documents/University/MOD2 (MOD3 UT)/FinalAssignment/";
		
		try {
			DatagramSocket serverSocket = new DatagramSocket(8888);
			
			byte[] buffIn = new byte[25000];
			DatagramPacket receiveFileContent = new DatagramPacket(buffIn, buffIn.length);
			serverSocket.receive(receiveFileContent);

			String fileName = "testFile0";
			File file = new File(savePath + fileName);
			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(receiveFileContent.getData());
			System.out.println("Writing data to file on server");
			outputStream.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		int num = 250;
//
//		try {
//			DatagramSocket socket = new DatagramSocket();
//			System.out.println("made socket");
//			ByteArrayOutputStream bout = new ByteArrayOutputStream();
//			System.out.println("made bytearrayoutputstream");
//			bout.write(num);
//			System.out.println("added number");
//			byte[] barray = bout.toByteArray();
//			System.out.println("made array");
//			InetAddress remote_addr = InetAddress.getByName("localhost");    
//			System.out.println("got address");
//			DatagramPacket packet = new DatagramPacket( barray, barray.length, remote_addr, 8888);
//			System.out.println("made packet");
//			socket.send( packet );
//			System.out.println("send packet");
//			bout.close();
//		} catch (SocketException e) {
//			System.out.println(e.getMessage());
//		} catch (UnknownHostException e) {
//			System.out.println(e.getMessage());
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
//
//		try {
//			DatagramSocket socket1 = new DatagramSocket(8888);
//			DatagramPacket packet1 = new DatagramPacket(new byte[256] , 256);
//
//			socket1.receive(packet1);
//
//			ByteArrayInputStream bin = new ByteArrayInputStream(packet1.getData());
//
//			for (int i=0; i< packet1.getLength(); i++) {
//				int data = bin.read();
//				if(data == -1) {
//					System.out.println("did not work");
//					break;
//				} else {
//					System.out.print((int) data);
//				}
//			}
//		} catch (SocketException e) {
//			System.out.println(e.getMessage());
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
	}
}