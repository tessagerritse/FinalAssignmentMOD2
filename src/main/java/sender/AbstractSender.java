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

    public void sendPacketWithoutAck(DatagramSocket socket, DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    public abstract void send(DatagramSocket socket, InetAddress address, int port, byte[] content) throws IOException;
}
