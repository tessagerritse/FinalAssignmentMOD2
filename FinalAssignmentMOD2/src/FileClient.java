import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FileClient {

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
			
			while (true) {
				byte[] buffOut = new byte[25000];
				DatagramPacket request = new DatagramPacket(buffOut, buffOut.length, address, port);
				socket.send(request);
				
				byte[] buffIn = new byte[512];
				DatagramPacket response = new DatagramPacket(buffIn, buffIn.length);
				socket.receive(response);
				
				String answer = new String(buffIn, 0, response.getLength());
				System.out.println(answer);
				System.out.println();
			}
		} catch (SocketException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}

		
	}
}