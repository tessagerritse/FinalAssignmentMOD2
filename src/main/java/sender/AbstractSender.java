package sender;

import shared.Protocol;
import shared.Receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class AbstractSender {

    public void sendPacketWaitForAck(DatagramSocket socket, DatagramPacket packet) {
        boolean acked = false;
        while (!acked) {
            try {
                socket.send(packet);
                Receiver.receiveAck(socket);
                acked = true;
            } catch (IOException e) {
                acked = false;
            }
        }
    }

    public void sendAck(DatagramSocket socket, InetAddress address, int port) throws IOException {
        byte[] ack = new byte[Protocol.ACK_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(ack, ack.length, address, port);
        socket.send(packet);
    }

    public abstract void send(DatagramSocket socket, InetAddress address, int port, byte[] content);
}
