package at.stefl.gatekeeper.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareDoorFactory;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.exception.IntercomLockedException;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;
import at.stefl.gatekeeper.shared.inteface.Remote;

// TODO: synchronize listeners
public class Server {

	String name;
	private final HardwareDoorFactory doorFactory;
	private final Map<String, HardwareDoor> doors;
	private final Collection<HardwareDoor> doorsRead;

	private final Set<ServerRemote> remotes;
	private final Map<Intercom, ServerRemote> intercomLocks;

	public Server(HardwareDoorFactory doorFactory) {
		this.doorFactory = doorFactory;
		this.doors = new HashMap<String, HardwareDoor>();
		this.doorsRead = Collections.unmodifiableCollection(this.doors.values());

		this.remotes = new HashSet<ServerRemote>();
		this.intercomLocks = new HashMap<Intercom, ServerRemote>();
	}

	public String getName() {
		return name;
	}

	public Collection<HardwareDoor> getDoors() {
		return doorsRead;
	}

	public Door getDoor(String door) {
		return doors.get(door);
	}

	public void init(ServerConfig config) {
		this.name = config.name;

		for (ServerConfig.Door doorConfig : config.doors) {
			HardwareDoor door = doorFactory.create(doorConfig);
			door.init();
			doors.put(doorConfig.name, door);
		}
	}

	public Remote createRemote() {
		ServerRemote remote = new ServerRemote(this);
		synchronized (remotes) {
			remotes.add(remote);
		}
		return remote;
	}

	void destroyRemote(ServerRemote remote) {
		synchronized (remotes) {
			remotes.remove(remote);
		}
	}

	public void start() {
	}

	public void close() {
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
