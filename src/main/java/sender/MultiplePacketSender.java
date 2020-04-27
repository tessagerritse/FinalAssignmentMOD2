package sender;

import shared.PacketManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class MultiplePacketSender extends AbstractSender {

    @Override
    public void send(DatagramSocket socket, InetAddress address, int port, byte[] content) {
        List<byte[]> listOfPackets = PacketManager.makeMultiplePackets(content);

        for (byte[] singlePacket : listOfPackets) {
            DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
            sendPacketWaitForAck(socket, packet);
        }
    }
}
