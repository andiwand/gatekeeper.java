package at.stefl.gatekeeper.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.network.json.GsonPacketHelper;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;

public class BroadcastReceiver {

	private final DatagramSocket broadcastSocket;
	private final byte[] buffer;
	private final DatagramPacket packet;
	private final ByteArrayInputStream ain;
	private final GsonPacketHelper packetHelper;

	public BroadcastReceiver() throws SocketException {
		this(Constants.BROADCAST_PORT);
	}

	private BroadcastReceiver(int port) throws SocketException {
		this.broadcastSocket = new DatagramSocket(port);
		this.buffer = new byte[Constants.BROADCAST_BUFFER];
		this.packet = new DatagramPacket(this.buffer, this.buffer.length);
		this.ain = new ByteArrayInputStream(buffer, 0, packet.getLength());
		this.packetHelper = new GsonPacketHelper();
	}

	public StandardPacket receive() throws IOException {
		ain.reset();
		broadcastSocket.receive(packet);
		return packetHelper.fromJson(new InputStreamReader(ain, Constants.CHARSET));
	}

}
