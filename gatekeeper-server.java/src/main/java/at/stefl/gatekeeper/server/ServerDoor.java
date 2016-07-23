package at.stefl.gatekeeper.server;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.shared.inteface.AbstractDoor;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class ServerDoor extends AbstractDoor {

	final Server server;
	final ServerRemote remote;
	final HardwareDoor door;
	final ServerIntercom intercom;

	private final Door.Listener listener = new Door.Listener() {
		public void bell(Door door) {
			fireBell();
		}

		public void unlocked(Door door) {
			fireUnlocked();
		}
	};

	public ServerDoor(ServerRemote remote, HardwareDoor door) {
		this.remote = remote;
		this.server = remote.server;
		this.door = door;
		this.intercom = new ServerIntercom(this, door.getIntercom());

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
	void closeInterface() {
		remote.checkClosed();
		intercom.closeInterface();
	}

}
