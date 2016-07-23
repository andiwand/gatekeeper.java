package at.stefl.gatekeeper.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.shared.exception.RemoteClosedException;
import at.stefl.gatekeeper.shared.inteface.AbstractRemote;
import at.stefl.gatekeeper.shared.inteface.Door;

public class ServerRemote extends AbstractRemote {

	final Server server;

	private final Map<String, ServerDoor> doors;
	private final Collection<? extends Door> doorsRead;

	final Object lock;
	private boolean closed;

	public ServerRemote(Server server) {
		this.server = server;

		this.doors = new HashMap<String, ServerDoor>();
		this.doorsRead = Collections.unmodifiableCollection(this.doors.values());

		this.lock = new Object();
		this.closed = false;

		for (HardwareDoor door : server.getDoors()) {
			ServerDoor doorInterface = new ServerDoor(this, door);
			this.doors.put(door.getName(), doorInterface);
		}
	}

	public String getName() {
		checkClosed();
		return server.name;
	}

	public Collection<? extends Door> getDoors() {
		checkClosed();
		return doorsRead;
	}

	public Door getDoor(String door) {
		checkClosed();
		return doors.get(door);
	}

	public void checkClosed() {
		if (closed)
			throw new RemoteClosedException();
	}

	public void close() {
		close(true);
	}

	// TODO: clear or set properties null?
	// TODO: clear listeners?
	public void close(boolean client) {
		synchronized (lock) {
			if (closed)
				return;

			for (ServerDoor door : doors.values()) {
				door.closeInterface();
			}

			doors.clear();

			server.destroyRemote(this);
			closed = true;
			fireClosed(this, client);
		}
	}

}
