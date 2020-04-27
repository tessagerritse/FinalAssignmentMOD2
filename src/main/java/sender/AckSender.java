package sender;

import shared.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AckSender extends AbstractSender {

    @Override
    public void send(DatagramSocket socket, InetAddress address, int port, byte[] content) throws IOException {
        byte[] ack = new byte[Protocol.ACK_PACKET_SIZE];
        DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, address, port);
        sendPacketWithoutAck(socket, ackPacket);
    }
}
