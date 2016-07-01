package at.stefl.gatekeeper.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.exception.IntercomLockedException;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;
import at.stefl.gatekeeper.shared.inteface.Remote;
import at.stefl.gatekeeper.shared.network.json.GsonPacketHelper;
import at.stefl.gatekeeper.shared.network.packet.DoorRequest;
import at.stefl.gatekeeper.shared.network.packet.InfoResponse;
import at.stefl.gatekeeper.shared.network.packet.SimpleResponse;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;

public class WebSocketRemote implements Remote {

	private class Client extends WebSocketClient {
		public Client(URI uri) {
			super(uri);
		}

		@Override
		public void onOpen(ServerHandshake handshake) {
		}

		@Override
		public void onClose(int code, String reason, boolean remote) {
		}

		@Override
		public void onError(Exception exception) {
		}

		@Override
		public void onMessage(String message) {
			StandardPacket packet = packetHelper.fromJson(message);
			handlePacket(packet);
		}

		@Override
		public void onMessage(ByteBuffer bytes) {
			handleBinary(bytes);
		}

		private void sendPacket(StandardPacket packet) {
			String message = packetHelper.toJson(packet);
			send(message);
		}

		private void sendPacket(StandardPacket.Type type, Object payload) {
			StandardPacket packet = new StandardPacket();
			packet.setType(type);
			packet.setPayload(payload);
			sendPacket(packet);
		}
	}

	private class DoorImpl implements Door {
		private final String name;
		private final IntercomImpl intercom;
		private final boolean hasBell;
		private final boolean hasUnlock;
		private final Set<Door.Listener> listeners;

		public DoorImpl(String name, boolean hasBell, boolean hasUnlock, boolean hasIntercom) {
			this.name = name;
			this.intercom = hasIntercom ? new IntercomImpl(this) : null;
			this.hasBell = hasBell;
			this.hasUnlock = hasUnlock;
			this.listeners = new HashSet<Door.Listener>();
		}

		public String getName() {
			return name;
		}

		public Intercom getIntercom() {
			return intercom;
		}

		public boolean hasBell() {
			return hasBell;
		}

		public boolean hasUnlock() {
			return hasUnlock;
		}

		public boolean hasIntercom() {
			return intercom != null;
		}

		public void addListener(Listener listener) {
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			listeners.remove(listener);
		}

		public void unlock() {
			if (!hasUnlock) {
				// TODO: exception
				return;
			}

			requestUnlock(name);
		}
	}

	private class IntercomImpl implements Intercom {
		private final DoorImpl door;
		private final Set<Intercom.Listener> listeners;

		public IntercomImpl(DoorImpl door) {
			this.door = door;
			this.listeners = new HashSet<Intercom.Listener>();
		}

		public boolean isOpen() {
			synchronized (intercomLock) {
				return openIntercom == this;
			}
		}

		public void addListener(Listener listener) {
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			listeners.remove(listener);
		}

		public AudioOutputStream open(AudioOutputStream microphone) {
			synchronized (intercomLock) {
				if (openIntercom == this)
					throw new IntercomLockedException(); // TODO: additional
															// info?
				if (openIntercom != null)
					throw new IntercomLockedException();

				SimpleResponse response = requestIntercom(door.name);
				switch (response.getStatus()) {
				case ERROR:
					// TODO: exception
					return null;
				case WARNING:
					// TODO: output
					break;
				case OK:
					break;
				}

				openIntercom = this;
				WebSocketRemote.this.intercomMicrophone = microphone;
				intercomSpeaker = new SpeakerStream();
				return intercomSpeaker;
			}
		}

		public void close() {
			synchronized (intercomLock) {
				// TODO: exception?
				if (openIntercom != this)
					return;

				closeIntercom();

				openIntercom = null;
				intercomMicrophone = null;
				intercomSpeaker.close();
				intercomSpeaker = null;
			}
		}
	}

	private class SpeakerStream extends AudioOutputStream {
		private boolean closed;

		@Override
		public void write(ByteBuffer bytes) {
			// TODO: exception?
			if (closed)
				return;
			socket.send(bytes);
		}

		public void close() {
			closed = true;
		}
	}

	private Client client;
	private WebSocket socket;
	private final GsonPacketHelper packetHelper;

	private EnumSet<StandardPacket.Type> responseType;
	private final Object responseLock;
	private Object response;

	private String name;
	private final Map<String, DoorImpl> doors;
	private final Collection<DoorImpl> doorsRead;

	private final Object intercomLock;
	private IntercomImpl openIntercom;
	private AudioOutputStream intercomMicrophone;
	private SpeakerStream intercomSpeaker;

	private final Set<Remote.Listener> listeners;

	public WebSocketRemote() {
		this.packetHelper = new GsonPacketHelper();
		this.responseLock = new Object();
		this.doors = new HashMap<String, WebSocketRemote.DoorImpl>();
		this.doorsRead = Collections.unmodifiableCollection(this.doors.values());
		this.intercomLock = new Object();
		this.listeners = new HashSet<Remote.Listener>();
	}

	public String getName() {
		return name;
	}

	public Collection<? extends Door> getDoors() {
		return doorsRead;
	}

	public Door getDoor(String door) {
		return doors.get(door);
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void connect(String host) {
		connect(host, Constants.WEBSOCKET_PORT);
	}

	private void connect(String host, int port) {
		URI uri;

		try {
			uri = new URI(Constants.WEBSOCKET_SCHEME, null, host, port, null, null, null);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}

		client = new Client(uri);
		client.connect();
		socket = client.getConnection();

		InfoResponse infoResponse = requestInfo();
		name = infoResponse.name;
		for (Map.Entry<String, InfoResponse.Door> entry : infoResponse.doors.entrySet()) {
			DoorImpl door = new DoorImpl(entry.getKey(), entry.getValue().hasBell, entry.getValue().hasUnlock,
					entry.getValue().hasIntercom);
			doors.put(entry.getKey(), door);
		}
	}

	public void close() {
		client.close();
	}

	private void waitPacket() {
		synchronized (responseLock) {
			if (response != null)
				return;

			try {
				responseLock.wait();
			} catch (InterruptedException e) {
				// TODO: destroy
				throw new IllegalStateException(e);
			}

			if (response == null) {
				throw new IllegalStateException("no response");
			}
		}
	}

	private Object request(StandardPacket.Type type, Object payload, EnumSet<StandardPacket.Type> responseType) {
		this.response = null;
		this.responseType = responseType;
		client.sendPacket(type, payload);
		waitPacket();
		return response;
	}

	private InfoResponse requestInfo() {
		return (InfoResponse) request(StandardPacket.Type.REQUEST_INFO, null,
				EnumSet.of(StandardPacket.Type.RESPONSE_INFO));
	}

	private SimpleResponse requestUnlock(String door) {
		DoorRequest request = new DoorRequest();
		request.setDoor(door);
		return (SimpleResponse) request(StandardPacket.Type.REQUEST_UNLOCK, request,
				EnumSet.of(StandardPacket.Type.RESPONSE_SIMPLE));
	}

	private SimpleResponse requestIntercom(String door) {
		DoorRequest request = new DoorRequest();
		request.setDoor(door);
		return (SimpleResponse) request(StandardPacket.Type.REQUEST_INTERCOM, request,
				EnumSet.of(StandardPacket.Type.RESPONSE_SIMPLE));
	}

	private void closeIntercom() {
		client.sendPacket(StandardPacket.Type.CLOSE_INTERCOM, null);
	}

	private void handlePacket(StandardPacket packet) {
		synchronized (responseLock) {
			if (responseType.contains(packet.getType())) {
				response = packet.getPayload();
				responseLock.notifyAll();
				return;
			}
		}

		switch (packet.getType()) {
		case CLOSE_INTERCOM: {
			// TODO: close if open
			break;
		}
		default: {
			// TODO: react
			System.out.println("unhandled type: " + packet.getType());
			break;
		}
		}
	}

	private void handleBinary(ByteBuffer bytes) {
		if (openIntercom == null) {
			// TODO: react
			return;
		}

		intercomMicrophone.write(bytes);
	}

}
