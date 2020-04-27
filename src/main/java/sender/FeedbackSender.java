package sender;

import shared.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FeedbackSender extends AbstractSender{

    @Override
    public void send(DatagramSocket socket, InetAddress address, int port, byte[] content) throws IOException {
        DatagramPacket feedbackPacket = new DatagramPacket(content, content.length,
                address, port);
        sendPacketWithoutAck(socket, feedbackPacket);
    }
}
