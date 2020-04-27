package sender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HeaderlessPacketSender extends AbstractSender{

    @Override
    public void send(DatagramSocket socket, InetAddress address, int port, byte[] content) {
        DatagramPacket namePacket = new DatagramPacket(content, content.length, address, port);
        sendPacketWaitForAck(socket, namePacket);
    }
}
