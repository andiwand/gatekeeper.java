package at.stefl.gatekeeper.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.exception.IntercomLockedException;
import at.stefl.gatekeeper.shared.exception.IntercomNotAccessibleException;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;
import at.stefl.gatekeeper.shared.inteface.Remote;
import at.stefl.gatekeeper.shared.network.json.GsonPacketHelper;
import at.stefl.gatekeeper.shared.network.packet.DoorRequest;
import at.stefl.gatekeeper.shared.network.packet.InfoResponse;
import at.stefl.gatekeeper.shared.network.packet.SimpleResponse;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;

// TODO: fix close handling
public class WebSocketService extends WebSocketServer {

	private class Worker {
		private class IntercomMicrophoneStream extends AudioOutputStream {
			@Override
			public void write(ByteBuffer bytes) {
				synchronized (Worker.this) {
					if (openIntercom == null)
						return;
					socket.send(bytes);
				}
			}
		}

		private final WebSocket socket;
		private final Remote remote;
		private final GsonPacketHelper packetHelper;
		private Intercom openIntercom;
		private AudioOutputStream intercomSpeaker;
		private AudioOutputStream intercomMicrophone;

		private final Remote.Listener listener = new Remote.Listener() {
			public void closed(Remote serverInterface, boolean client) {
				// TODO: ????
				if (socket.isClosing() | socket.isClosed())
					return;

				// TODO: add code and message
				socket.close();
			}
		};

		public Worker(WebSocket socket, Remote remote) {
			this.socket = socket;
			this.remote = remote;
			this.packetHelper = new GsonPacketHelper();
			this.remote.addListener(listener);
			this.intercomMicrophone = new IntercomMicrophoneStream();
		}

		public synchronized void handleMessage(String message) {
			StandardPacket packet = packetHelper.fromJson(message);

			switch (packet.getType()) {
			case REQUEST_INFO: {
				sendPacket(StandardPacket.Type.RESPONSE_INFO, infoResponse);
				break;
			}
			case REQUEST_UNLOCK: {
				String doorName = ((DoorRequest) packet.getPayload()).getDoor();
				Door door = remote.getDoor(doorName);
				if (door == null) {
					// TODO: send error
					sendSimpleResponse(SimpleResponse.Status.ERROR, 0, null);
					break;
				}
				if (!door.hasUnlock()) {
					// TODO: send error
					sendSimpleResponse(SimpleResponse.Status.ERROR, 0, null);
					break;
				}
				door.unlock();
				sendSimpleResponse(SimpleResponse.Status.OK, 0, null);
				break;
			}
			case REQUEST_INTERCOM: {
				if (openIntercom != null) {
					// TODO: send error
					sendSimpleResponse(SimpleResponse.Status.ERROR, 0, null);
					break;
				}
				String doorName = ((DoorRequest) packet.getPayload()).getDoor();
				Door door = remote.getDoor(doorName);
				if (door == null) {
					// TODO: send error
					sendSimpleResponse(SimpleResponse.Status.ERROR, 0, null);
					break;
				}
				if (!door.hasIntercom()) {
					// TODO: send error
					sendSimpleResponse(SimpleResponse.Status.ERROR, 0, null);
					break;
				}
				Intercom intercom = door.getIntercom();
				try {
					intercomSpeaker = intercom.open(intercomMicrophone);
				} catch (IntercomLockedException e) {
					// TODO: send error
					sendSimpleResponse(SimpleResponse.Status.ERROR, 0, null);
					break;
				} catch (IntercomNotAccessibleException e) {
					// TODO: send error
					sendSimpleResponse(SimpleResponse.Status.ERROR, 0, null);
					break;
				}
				openIntercom = intercom;
				sendSimpleResponse(SimpleResponse.Status.OK, 0, null);
				break;
			}
			case CLOSE_INTERCOM: {
				if (openIntercom == null) {
					// TODO: error
					break;
				}
				openIntercom.close();
				openIntercom = null;
				// TODO: send close back?
				break;
			}
			default: {
				System.out.println("unhandled type: " + packet.getType());
				// TODO: code and message
				close(0, null);
				break;
			}
			}
		}

		public synchronized void handleBinary(ByteBuffer bytes) {
			if (openIntercom == null) {
				// TODO: code and message
				close(0, null);
				return;
			}

			intercomSpeaker.write(bytes);
		}

		public synchronized void handleClose(int code, String message, boolean client) {
			if (client) {
				// TODO: add code and message
				remote.close();
			}
		}

		public synchronized void close(int code, String message) {
			remote.close();
		}

		private synchronized void sendPacket(StandardPacket packet) {
			String message = packetHelper.toJson(packet);
			socket.send(message);
		}

		private void sendPacket(StandardPacket.Type type, Object payload) {
			StandardPacket packet = new StandardPacket();
			packet.setType(type);
			packet.setPayload(payload);
			sendPacket(packet);
		}

		private void sendSimpleResponse(SimpleResponse.Status status, int code, String message) {
			SimpleResponse response = new SimpleResponse();
			response.setStatus(status);
			response.setCode(code);
			response.setMessage(message);
			StandardPacket packet = new StandardPacket();
			packet.setType(StandardPacket.Type.RESPONSE_SIMPLE);
			packet.setPayload(response);
			sendPacket(packet);
		}
	}

	private final Server server;
	private final Map<WebSocket, Worker> workers;

	private final InfoResponse infoResponse;

	public WebSocketService(Server server) {
		this(server, Constants.WEBSOCKET_PORT);
	}

	private WebSocketService(Server server, int port) {
		super(new InetSocketAddress(port));

		this.server = server;
		this.workers = new HashMap<WebSocket, WebSocketService.Worker>();

		// TODO: move
		infoResponse = new InfoResponse();
		infoResponse.name = server.getName();
		infoResponse.doors = new HashMap<String, InfoResponse.Door>();
		for (Door door : server.getDoors()) {
			InfoResponse.Door doorInfo = new InfoResponse.Door();
			doorInfo.hasBell = door.hasBell();
			doorInfo.hasUnlock = door.hasUnlock();
			doorInfo.hasIntercom = door.hasIntercom();
			infoResponse.doors.put(door.getName(), doorInfo);
		}
	}

	@Override
	public void onOpen(WebSocket socket, ClientHandshake handshake) {
		Remote serverInerface = server.createRemote();
		Worker worker = new Worker(socket, serverInerface);

		synchronized (workers) {
			workers.put(socket, worker);
		}
	}

	@Override
	public void onClose(WebSocket socket, int code, String reason, boolean remote) {
		Worker worker;
		synchronized (workers) {
			worker = workers.remove(socket);
		}
		worker.handleClose(code, reason, remote);
	}

	@Override
	public void onError(WebSocket socket, Exception exception) {
	}

	@Override
	public void onMessage(WebSocket socket, String message) {
		Worker worker;
		synchronized (workers) {
			worker = workers.get(socket);
		}
		worker.handleMessage(message);
	}

	@Override
	public void onMessage(WebSocket socket, ByteBuffer message) {
		Worker worker;
		synchronized (workers) {
			worker = workers.get(socket);
		}
		worker.handleBinary(message);
	}

}
