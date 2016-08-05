package at.stefl.gatekeeper.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;
import at.stefl.gatekeeper.shared.inteface.Remote;
import at.stefl.gatekeeper.shared.network.json.GsonPacketHelper;
import at.stefl.gatekeeper.shared.network.packet.BeaconSignal;
import at.stefl.gatekeeper.shared.network.packet.DoorSignal;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;
import at.stefl.gatekeeper.shared.util.ByteArrayOutputStream;
import at.stefl.gatekeeper.shared.util.NetworkInterfaces;

public class BroadcastStation {

	private final Remote remote;

	private DatagramSocket broadcastSocket;
	private final List<InetAddress> broadcastAddresses;
	private final int port;

	private final Deque<StandardPacket> packetQueue;
	private final GsonPacketHelper packetHelper;
	private final byte[] buffer;
	private final ByteArrayOutputStream aout;
	private Thread senderThread;

	private final Runnable sender = new Runnable() {
		public void run() {
			while (true) {
				try {
					synchronized (packetQueue) {
						if (packetQueue.isEmpty())
							packetQueue.wait();
						StandardPacket packet = packetQueue.removeFirst();
						sendImpl(packet);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private final Door.Listener doorListener = new Door.Listener() {
		public void unlocked(Door door) {
			sendDoorSignal(door, DoorSignal.Type.UNLOCKED);
		}

		public void bell(Door door) {
			sendDoorSignal(door, DoorSignal.Type.BELL);
		}
	};

	private final Intercom.Listener intercomListener = new Intercom.Listener() {
		public void opened(Intercom intercom) {
			sendDoorSignal(intercom.getDoor(), DoorSignal.Type.TOOK_OFF);
		}

		public void closed(Intercom intercom) {
			sendDoorSignal(intercom.getDoor(), DoorSignal.Type.HUNG_UP);
		}
	};

	public BroadcastStation(Remote remote) throws SocketException {
		this(NetworkInterfaces.getBroadcastAddresses(), remote);
	}

	public BroadcastStation(Collection<InetAddress> broadcastAdresses, Remote remote) throws SocketException {
		this(Constants.BROADCAST_PORT, broadcastAdresses, remote);
	}

	private BroadcastStation(int port, Collection<InetAddress> broadcastAddresses, Remote remote) {
		this.remote = remote;

		this.port = port;
		this.broadcastAddresses = new ArrayList<InetAddress>(broadcastAddresses);

		this.packetQueue = new LinkedList<StandardPacket>();
		this.packetHelper = new GsonPacketHelper();
		this.buffer = new byte[Constants.BROADCAST_BUFFER];
		this.aout = new ByteArrayOutputStream(this.buffer);
	}

	public void init() throws SocketException {
		broadcastSocket = new DatagramSocket();

		for (Door door : remote.getDoors()) {
			door.addListener(doorListener);
			if (door.hasIntercom())
				door.getIntercom().addListener(intercomListener);
		}

		senderThread = new Thread(sender);
		senderThread.start();
	}

	private void sendImpl(StandardPacket packet) throws IOException {
		aout.reset();
		packetHelper.toJson(packet, new OutputStreamWriter(aout, Constants.CHARSET));
		DatagramPacket datagramPacket = new DatagramPacket(buffer, aout.getSize());

		for (InetAddress address : broadcastAddresses) {
			datagramPacket.setAddress(address);
			datagramPacket.setPort(port);
			broadcastSocket.send(datagramPacket);
		}
	}

	private void send(StandardPacket.Type type, Object payload) {
		StandardPacket packet = new StandardPacket();
		packet.setType(type);
		packet.setPayload(payload);
		send(packet);
	}

	private void send(StandardPacket packet) {
		synchronized (packetQueue) {
			packetQueue.addLast(packet);
			packetQueue.notify();
		}
	}

	public void sendBeaconSignal() {
		BeaconSignal signal = new BeaconSignal();
		signal.setName(remote.getName());
		send(StandardPacket.Type.SIGNAL_BEACON, signal);
	}

	public void sendDoorSignal(Door door, DoorSignal.Type type) {
		DoorSignal signal = new DoorSignal();
		signal.setDoor(door.getName());
		signal.setSignalType(type);
		send(StandardPacket.Type.SIGNAL_DOOR, signal);
	}

}
