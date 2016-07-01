package at.stefl.gatekeeper.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.inteface.Remote;
import at.stefl.gatekeeper.shared.network.json.GsonPacketHelper;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;
import at.stefl.gatekeeper.shared.util.ByteArrayOutputStream;
import at.stefl.gatekeeper.shared.util.NetworkInterfaces;

public class BroadcastStation {

	private final Remote remote;

	private final DatagramSocket broadcastSocket;
	private final List<InetAddress> broadcastAddresses;
	private final int port;

	private final GsonPacketHelper packetHelper;
	private final byte[] buffer;
	private final ByteArrayOutputStream aout;

	public BroadcastStation(Remote remote) throws SocketException {
		this(NetworkInterfaces.getBroadcastAddresses(), remote);
	}

	public BroadcastStation(Collection<InetAddress> broadcastAdresses, Remote remote) throws SocketException {
		this(Constants.BROADCAST_PORT, broadcastAdresses, remote);
	}

	private BroadcastStation(int port, Collection<InetAddress> broadcastAddresses, Remote remote)
			throws SocketException {
		this.remote = remote;

		this.broadcastSocket = new DatagramSocket();
		this.broadcastAddresses = new ArrayList<InetAddress>(broadcastAddresses);
		this.port = port;

		this.packetHelper = new GsonPacketHelper();
		this.buffer = new byte[Constants.BROADCAST_BUFFER];
		this.aout = new ByteArrayOutputStream(this.buffer);
	}

	public void send(StandardPacket packet) throws IOException {
		aout.reset();
		packetHelper.toJson(packet, new OutputStreamWriter(aout, Constants.CHARSET));
		DatagramPacket datagramPacket = new DatagramPacket(buffer, aout.getSize());

		for (InetAddress address : broadcastAddresses) {
			datagramPacket.setAddress(address);
			datagramPacket.setPort(port);
			broadcastSocket.send(datagramPacket);
		}
	}

}
