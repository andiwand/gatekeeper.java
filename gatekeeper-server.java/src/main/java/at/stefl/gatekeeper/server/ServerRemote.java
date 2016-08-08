package at.stefl.gatekeeper.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.stefl.gatekeeper.shared.exception.RemoteClosedException;
import at.stefl.gatekeeper.shared.inteface.AbstractRemote;
import at.stefl.gatekeeper.shared.inteface.Door;

public class ServerRemote extends AbstractRemote {

	final Server server;

	private final Map<String, ServerDoor> doors;
	private final Collection<ServerDoor> doorsRead;

	final Object lock;
	private boolean closed;

	public ServerRemote(Server server) {
		this.server = server;

		this.doors = new HashMap<String, ServerDoor>();
		this.doorsRead = Collections.unmodifiableCollection(this.doors.values());

		this.lock = new Object();
		this.closed = false;
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

	public void close() {
		close(true);
	}

	void addDoor(ServerDoor door) {
		doors.put(door.name, door);
	}

	void checkClosed() {
		if (closed)
			throw new RemoteClosedException();
	}

	ServerRemote fork() {
		ServerRemote result = new ServerRemote(server);
		for (ServerDoor door : doorsRead) {
			result.addDoor(door.fork());
		}
		return result;
	}

	// TODO: clear or set properties null?
	// TODO: clear listeners?
	void close(boolean client) {
		synchronized (lock) {
			if (closed)
				return;

			for (ServerDoor door : doorsRead) {
				door.closeInterface();
			}

			doors.clear();

			server.removeRemote(this);
			closed = true;
			fireClosed(this, client);
		}
	}

}
