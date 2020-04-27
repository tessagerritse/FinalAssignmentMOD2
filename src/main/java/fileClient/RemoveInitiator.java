package fileClient;

import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.Utils;
import shared.Protocol;
import shared.Sender;

/**
 * Sends a remove request of a certain file when user commands.
 *
 * @author tessa.gerritse
 */
public class RemoveInitiator implements Runnable {

    private final FileClientTUI view;
    private final DatagramSocket removeSocket;
    private final InetAddress serverAddress;
    private final String fileName;

    public RemoveInitiator(FileClientTUI view, DatagramSocket removeSocket, InetAddress serverAddress,
                           String fileName) {
        this.view = view;
        this.removeSocket = removeSocket;
        this.serverAddress = serverAddress;
        this.fileName = fileName;
    }

    @Override
    /**
     * Sends name of the file that has to be removed, receives feedback if it was successful or not,
     * and displays feedback
     */
    public void run() {
        byte[] nameBytes = Utils.getBytesFromString(fileName);
        Sender.sendNamePacket(removeSocket, serverAddress, Protocol.REMOVE_PORT, nameBytes);
		view.showMessage("Sent request to server to remove " + fileName + ". \n");
    }

}
