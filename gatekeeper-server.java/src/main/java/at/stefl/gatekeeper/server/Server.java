package at.stefl.gatekeeper.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.exception.IntercomLockedException;
import at.stefl.gatekeeper.shared.exception.RemoteClosedException;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;
import at.stefl.gatekeeper.shared.inteface.Remote;
import at.stefl.gatekeeper.shared.util.ListenerUtil;

// TODO: synchronize listeners
public class Server implements Remote {

	private class RemoteImpl implements Remote {
		private final Map<String, DoorImpl> doors;
		private final Collection<? extends Door> doorsRead;
		private final List<Remote.Listener> listeners;

		private final Object lock;
		private boolean closed;

		public RemoteImpl() {
			this.doors = new HashMap<String, Server.DoorImpl>();
			this.doorsRead = Collections.unmodifiableCollection(this.doors.values());
			this.listeners = new LinkedList<Remote.Listener>();

			this.lock = new Object();
			this.closed = false;

			for (HardwareDoor door : Server.this.getDoors()) {
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

		public void addListener(Listener listener) {
			checkClosed();
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			checkClosed();
			listeners.remove(listener);
		}

		public void checkClosed() {
			if (closed)
				throw new RemoteClosedException();
		}

		public void close() {
			close(true);
		}

		// TODO: clear or set properties null?
		public void close(boolean client) {
			synchronized (lock) {
				if (closed)
					return;

				for (DoorImpl door : doors.values()) {
					door.closeInterface();
				}

				doors.clear();
				listeners.clear();

				destroyRemote(this);
				closed = true;
				ListenerUtil.fireRemoteClosed(listeners, this, client);
			}
		}
	}

	private class DoorImpl implements Door {
		private final RemoteImpl remote;
		private final HardwareDoor door;
		private final IntercomImpl intercom;
		private final List<Door.Listener> listeners;

		private final Door.Listener listener = new Door.Listener() {
			public void bell(Door door) {
				ListenerUtil.fireDoorBell(listeners, door);
			}

			public void unlocked(Door door) {
				ListenerUtil.fireDoorUnlocked(listeners, door);
			}
		};

		public DoorImpl(RemoteImpl remote, HardwareDoor door) {
			this.remote = remote;
			this.door = door;
			this.intercom = new IntercomImpl(this, door.getIntercom());
			this.listeners = new LinkedList<Door.Listener>();

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

		public void addListener(Listener listener) {
			remote.checkClosed();
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			remote.checkClosed();
			listeners.remove(listener);
		}

		public void unlock() {
			synchronized (remote.lock) {
				remote.checkClosed();
				door.unlock();
			}
		}

		private void closeInterface() {
			remote.checkClosed();
			intercom.closeInterface();
			listeners.clear();

			synchronized (door) {
				this.door.removeListener(listener);
			}
		}
	}

	// TODO; synchronize on separate objects
	private class IntercomImpl implements Intercom {
		private final DoorImpl door;
		private final HardwareIntercom intercom;
		private final List<Intercom.Listener> listeners;

		private final Intercom.Listener listener = new Intercom.Listener() {
			public void opened(Intercom intercom) {
				ListenerUtil.fireIntercomOpened(listeners, intercom);
			}

			public void closed(Intercom intercom) {
				ListenerUtil.fireIntercomClosed(listeners, intercom);
			}
		};

		public IntercomImpl(DoorImpl doorInterface, HardwareIntercom intercom) {
			this.door = doorInterface;
			this.intercom = intercom;
			this.listeners = new LinkedList<Intercom.Listener>();

			synchronized (this.intercom) {
				this.intercom.addListener(listener);
			}
		}

		public boolean isOpen() {
			door.remote.checkClosed();
			return intercom.isOpen();
		}

		public void addListener(Listener listener) {
			door.remote.checkClosed();
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			door.remote.checkClosed();
			listeners.remove(listener);
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

		private void closeInterface() {
			door.remote.checkClosed();
			close();
			listeners.clear();

			synchronized (this.intercom) {
				this.intercom.removeListener(listener);
			}
		}
	}

	private String name;
	private final Map<String, HardwareDoor> doors;
	private final Collection<HardwareDoor> doorsRead;

	private final Set<RemoteImpl> remotes;
	private final Map<HardwareIntercom, RemoteImpl> intercomLocks;

	public Server() {
		this.doors = new HashMap<String, HardwareDoor>();
		this.doorsRead = Collections.unmodifiableCollection(this.doors.values());

		this.remotes = new HashSet<RemoteImpl>();
		this.intercomLocks = new HashMap<HardwareIntercom, RemoteImpl>();
	}

	public String getName() {
		return name;
	}

	public Collection<HardwareDoor> getDoors() {
		return doorsRead;
	}

	public HardwareDoor getDoor(String door) {
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
			HardwareIntercom intercom = null;
			if (doorConfig.intercom != null) {
				intercom = new HardwareIntercom(doorConfig.intercom.speaker, doorConfig.intercom.microphone);
				// TODO: set audio format
				intercom.init(Constants.AUDIO_FORMAT, Constants.AUDIO_BUFFER_SIZE, Constants.AUDIO_FORMAT,
						Constants.AUDIO_BUFFER_SIZE);
			}

			HardwareDoor door = new HardwareDoor(doorConfig.name, doorConfig.bellPin, doorConfig.unlockPin, intercom);
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
