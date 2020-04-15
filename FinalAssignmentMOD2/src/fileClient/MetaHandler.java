package fileClient;

import java.io.IOException;
import java.net.DatagramSocket;

import shared.FileActions;
import shared.Receiver;

public class MetaHandler implements Runnable {

	private FileClientTUI view;
	private DatagramSocket metaSocket;
	private DatagramSocket downloadSocket;
	private DatagramSocket listSocket;
	private boolean listen = true;

	public MetaHandler(FileClientTUI view, DatagramSocket metaSocket, DatagramSocket downloadSocket,
			DatagramSocket listSocket) {
		this.view = view;
		this.metaSocket = metaSocket;
		this.downloadSocket = downloadSocket;
		this.listSocket = listSocket;
	}

	public void setListen(boolean listen) {
		this.listen = listen;
	}

	@Override
	public void run() {
		while (listen) {
			try {
				byte[] feedbackBytes = Receiver.receiveFeedback(metaSocket);
				String feedback = FileActions.getStringFromBytes(feedbackBytes);

				view.showMessage("Message from server: " + feedback + "\n");

				if (feedback.contains("exist")) {
					downloadSocket.close();
				} else if (feedback.contains("empty")) {
					listSocket.close();
				}
			} catch (IOException e) {
				view.showMessage("IO exception at meta handler: " + e.getMessage());
			}
		}
	}
}
