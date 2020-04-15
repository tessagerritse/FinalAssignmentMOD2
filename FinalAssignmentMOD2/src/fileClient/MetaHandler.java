package fileClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import shared.Protocol;

public class MetaHandler implements Runnable {

	private FileClientTUI view;
	private DatagramSocket metaSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket listSocket;

	public MetaHandler(FileClientTUI view, DatagramSocket metaSocket, DatagramSocket downloadSocket,
			DatagramSocket listSocket) {
		this.view = view;
		this.metaSocket = metaSocket;
		this.downloadSocket = downloadSocket;
		this.listSocket = listSocket;
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] feedbackBytes = new byte[Protocol.FEEDBACK_PACKET_SIZE];
				DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, feedbackBytes.length);
				metaSocket.receive(feedbackPacket);
				
				String feedback = new String(feedbackPacket.getData()).trim();
				view.showMessage("Message from server: " + feedback + "\n");
			} catch (IOException e) {
				view.showMessage("IO exception at meta handler: " + e.getMessage());
			}
		}
	}
}
