package at.stefl.gatekeeper.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareDoorFactory;
import at.stefl.gatekeeper.server.hardware.HardwareFactory;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.server.hardware.HardwareIntercomFactory;
import at.stefl.gatekeeper.shared.exception.IntercomLockedException;

// TODO: synchronize listeners
public class Server {

	String name;
	private final HardwareFactory hardwareFactory;

	private ServerRemote remote;
	private final Set<ServerRemote> remotes;
	private final Map<HardwareIntercom, ServerRemote> intercomLocks;

	public Server(HardwareDoorFactory doorFactory, HardwareIntercomFactory intercomFactory) {
		this(new HardwareFactory(doorFactory, intercomFactory));
	}

	public Server(HardwareFactory hardwareFactory) {
		this.hardwareFactory = hardwareFactory;

		this.remotes = new HashSet<ServerRemote>();
		this.intercomLocks = new HashMap<HardwareIntercom, ServerRemote>();
	}

	public void init(ServerConfig config) {
		name = config.name;
		remote = new ServerRemote(this);

		for (ServerConfig.Door doorConfig : config.doors) {
			HardwareDoor hardwareDoor = hardwareFactory.createDoor(doorConfig);
			HardwareIntercom hardwareIntercom = (doorConfig.intercom == null) ? null
					: hardwareFactory.createIntercom(null);

			hardwareDoor.init();
			hardwareIntercom.init();

			ServerDoor door = new ServerDoor(remote, doorConfig.name, hardwareDoor, hardwareIntercom);
			remote.addDoor(door);
		}
	}

	public ServerRemote createRemote() {
		ServerRemote result = remote.fork();
		synchronized (remotes) {
			remotes.add(result);
		}
		return result;
	}

	public void start() {
	}

	public void close() {
	}

	void removeRemote(ServerRemote remote) {
		synchronized (remotes) {
			remotes.remove(remote);
		}
	}

	void reserveIntercom(ServerRemote remote, HardwareIntercom intercom) {
		synchronized (intercomLocks) {
			ServerRemote holder = intercomLocks.get(intercom);
			// TODO: additional info
			if (holder == remote)
				throw new IntercomLockedException();
			if (holder != null)
				throw new IntercomLockedException();
			intercomLocks.put(intercom, remote);
		}
	}

	void releaseIntercom(ServerRemote remote, HardwareIntercom intercom) {
		synchronized (intercomLocks) {
			ServerRemote holder = intercomLocks.get(intercom);
			// TODO: throw custom exceptions
			if (holder == null)
				throw new IllegalStateException();
			if (holder != remote)
				throw new IllegalStateException();
			intercomLocks.remove(intercom);
		}
	}

}
