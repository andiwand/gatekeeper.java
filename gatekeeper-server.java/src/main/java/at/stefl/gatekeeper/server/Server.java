package at.stefl.gatekeeper.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.stefl.gatekeeper.server.hardware.DoorFactory;
import at.stefl.gatekeeper.server.hardware.IntercomFactory;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.exception.IntercomLockedException;
import at.stefl.gatekeeper.shared.exception.RemoteClosedException;
import at.stefl.gatekeeper.shared.inteface.AbstractDoor;
import at.stefl.gatekeeper.shared.inteface.AbstractIntercom;
import at.stefl.gatekeeper.shared.inteface.AbstractRemote;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;
import at.stefl.gatekeeper.shared.inteface.Remote;

// TODO: synchronize listeners
public class Server implements Remote {

	private class RemoteImpl extends AbstractRemote {
		private final Map<String, DoorImpl> doors;
		private final Collection<? extends Door> doorsRead;

		private final Object lock;
		private boolean closed;

		public RemoteImpl() {
			this.doors = new HashMap<String, Server.DoorImpl>();
			this.doorsRead = Collections.unmodifiableCollection(this.doors.values());

			this.lock = new Object();
			this.closed = false;

			for (Door door : Server.this.getDoors()) {
				DoorImpl doorInterface = new DoorImpl(this, door);
				this.doors.put(door.getName(), doorInterface);
			}
		}

		public String getName() {
			checkClosed();
			return Server.this.name;
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

				for (DoorImpl door : doors.values()) {
					door.closeInterface();
				}

				doors.clear();

				destroyRemote(this);
				closed = true;
				fireClosed(this, client);
			}
		}
	}

	private class DoorImpl extends AbstractDoor {
		private final RemoteImpl remote;
		private final Door door;
		private final IntercomImpl intercom;

		private final Door.Listener listener = new Door.Listener() {
			public void bell(Door door) {
				fireBell(door);
			}

			public void unlocked(Door door) {
				fireUnlocked(door);
			}
		};

		public DoorImpl(RemoteImpl remote, Door door) {
			this.remote = remote;
			this.door = door;
			this.intercom = new IntercomImpl(this, door.getIntercom());

			synchronized (this.door) {
				this.door.addListener(listener);
			}
		}

		public String getName() {
			remote.checkClosed();
			return door.getName();
		}

		public Intercom getIntercom() {
			remote.checkClosed();
			return intercom;
		}

		public boolean hasBell() {
			remote.checkClosed();
			return door.hasBell();
		}

		public boolean hasUnlock() {
			remote.checkClosed();
			return door.hasUnlock();
		}

		public boolean hasIntercom() {
			remote.checkClosed();
			return door.hasIntercom();
		}

		public void unlock() {
			synchronized (remote.lock) {
				remote.checkClosed();
				door.unlock();
			}
		}

		// TODO: clear listeners?
		private void closeInterface() {
			remote.checkClosed();
			intercom.closeInterface();
		}
	}

	// TODO; synchronize on separate objects
	private class IntercomImpl extends AbstractIntercom {
		private final DoorImpl door;
		private final Intercom intercom;

		private final Intercom.Listener listener = new Intercom.Listener() {
			public void opened(Intercom intercom) {
				fireOpened(intercom);
			}

			public void closed(Intercom intercom) {
				fireClosed(intercom);
			}
		};

		public IntercomImpl(DoorImpl doorInterface, Intercom intercom) {
			this.door = doorInterface;
			this.intercom = intercom;

			synchronized (this.intercom) {
				this.intercom.addListener(listener);
			}
		}

		public boolean isOpen() {
			door.remote.checkClosed();
			return intercom.isOpen();
		}

		public AudioOutputStream open(AudioOutputStream microphone) {
			synchronized (door.remote.lock) {
				door.remote.checkClosed();
				AudioOutputStream result = null;

				synchronized (intercomLocks) {
					RemoteImpl holder = intercomLocks.get(intercom);
					if (holder == door.remote)
						throw new IntercomLockedException(); // TODO: additional
																// info?
					if (holder != null)
						throw new IntercomLockedException();
					result = intercom.open(microphone);
					if (result != null)
						intercomLocks.put(intercom, door.remote);
				}

				return result;
			}
		}

		public void close() {
			synchronized (door.remote.lock) {
				door.remote.checkClosed();

				synchronized (intercomLocks) {
					RemoteImpl holder = intercomLocks.get(intercom);
					// TODO: throw exceptions
					if (holder == null)
						return;
					if (holder != door.remote)
						return;
					intercom.close();
					intercomLocks.remove(intercom);
				}
			}
		}

		// TODO: clear listeners?
		private void closeInterface() {
			door.remote.checkClosed();
			close();
		}
	}

	private String name;
	private final Map<String, Door> doors;
	private final Collection<Door> doorsRead;

	private final Set<RemoteImpl> remotes;
	private final Map<Intercom, RemoteImpl> intercomLocks;

	public Server() {
		this.doors = new HashMap<String, Door>();
		this.doorsRead = Collections.unmodifiableCollection(this.doors.values());

		this.remotes = new HashSet<RemoteImpl>();
		this.intercomLocks = new HashMap<Intercom, RemoteImpl>();
	}

	public String getName() {
		return name;
	}

	public Collection<Door> getDoors() {
		return doorsRead;
	}

	public Door getDoor(String door) {
		return doors.get(door);
	}

	public void addListener(Listener listener) {
		throw new UnsupportedOperationException();
	}

	public void removeListener(Listener listener) {
		throw new UnsupportedOperationException();
	}

	public void init(ServerConfig config) {
		this.name = config.name;

		for (ServerConfig.Door doorConfig : config.doors) {
			Intercom intercom = null;
			if (doorConfig.intercom != null) {
				intercom = IntercomFactory.create(doorConfig.intercom.speaker, doorConfig.intercom.microphone);
				// TODO: set audio format
				// intercom.init(Constants.AUDIO_FORMAT,
				// Constants.AUDIO_BUFFER_SIZE, Constants.AUDIO_FORMAT,
				// Constants.AUDIO_BUFFER_SIZE);
			}

			Door door = DoorFactory.create(doorConfig.name, doorConfig.bellPin, doorConfig.unlockPin, intercom);
			doors.put(doorConfig.name, door);
		}
	}

	public Remote createRemote() {
		RemoteImpl remote = new RemoteImpl();
		synchronized (remotes) {
			remotes.add(remote);
		}
		return remote;
	}

	private void destroyRemote(RemoteImpl remote) {
		synchronized (remotes) {
			remotes.remove(remote);
		}
	}

	public void start() {
	}

	public void close() {
	}

}
