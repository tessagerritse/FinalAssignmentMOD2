package Client;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class FileClient {
	
	private static String path = "/Users/tessa.gerritse/SSHome/eclipse-workplace/FinalAssignmentMOD2_KLAD/"
			+ "src/exampleFiles/tiny.pdf";

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Syntax: FileClient <hostname> <port>");
			return;
		}

		String hostName = args[0];
		int port = Integer.parseInt(args[1]);

		try {
			InetAddress address = InetAddress.getByName(hostName);
			DatagramSocket socket = new DatagramSocket();
			
			//while (true) {				
				InputStream inputStream = new FileInputStream(path);
				byte[] buffOut = inputStream.readAllBytes();
				
				DatagramPacket request = new DatagramPacket(buffOut, buffOut.length, address, port);
				socket.send(request);
				
				byte[] buffIn = new byte[512];
				DatagramPacket response = new DatagramPacket(buffIn, buffIn.length);
				socket.receive(response);
				
				String answer = new String(buffIn, 0, response.getLength());
				System.out.println(answer);
				System.out.println();
			//}
		} catch (SocketException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}

		
	}
}