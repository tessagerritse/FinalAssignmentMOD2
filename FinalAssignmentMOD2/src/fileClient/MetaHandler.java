package fileClient;

import java.io.IOException;
import java.net.DatagramSocket;

import shared.DataActions;
import shared.Receiver;

/**
 * Starts at setup of client and does nothin else than listening 
 * for feedback from the server to display to the user.
 * 
 * @author tessa.gerritse
 *
 */
public class MetaHandler implements Runnable {

	private FileClientTUI view;
	private DatagramSocket metaSocket;
	private boolean listen = true;
	private boolean ableToDownload = true;
	private boolean ableToList = true;

	public MetaHandler(FileClientTUI view, DatagramSocket metaSocket) {
		this.view = view;
		this.metaSocket = metaSocket;
	}

	/*
	 * This method is called when the client quits, so its stops listening for meta data
	 */
	public void setListen(boolean listen) {
		this.listen = listen;
	}

	@Override
	public void run() {
		while (listen) {
			try {
				byte[] feedbackBytes = Receiver.receiveFeedback(metaSocket);
				String feedback = DataActions.getStringFromBytes(feedbackBytes);
				
				if (feedback.contains("exist")) {	
					ableToDownload = false;
				} else if (feedback.contains("empty")) {
					ableToList  = false;
				} else {
					view.showMessage("Message from server: " + feedback + "\n");
				}
			} catch (IOException e) {
				view.showMessage("IO exception at meta handler: " + e.getMessage());
			}
		}
	}

	public boolean ableToDownload() {
		return ableToDownload;
	}

	public boolean ableToList() {
		return ableToList;
	}
}
