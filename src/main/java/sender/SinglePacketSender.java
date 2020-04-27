package sender;

import shared.PacketManager;
import shared.Protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SinglePacketSender extends AbstractSender {

    @Override
    public void send(DatagramSocket socket, InetAddress address, int port, byte[] content) {
        byte[] singlePacket = PacketManager.makeSinglePacket(Protocol.EOF, content);
        DatagramPacket packet = new DatagramPacket(singlePacket, singlePacket.length, address, port);
        sendPacketWaitForAck(socket, packet);
    }
}
