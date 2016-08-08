package at.stefl.gatekeeper.server;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.inteface.AbstractDoor;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class ServerDoor extends AbstractDoor {

	final String name;
	final Server server;
	final ServerRemote remote;
	final HardwareDoor door;
	final ServerIntercom intercom;

	private final HardwareDoor.Listener listener = new HardwareDoor.Listener() {
		public void bell() {
			fireBell();
		}
	};

	public ServerDoor(ServerRemote remote, String name, HardwareDoor door, HardwareIntercom intercom) {
		this.name = "";
		this.remote = remote;
		this.server = remote.server;
		this.door = door;
		this.intercom = (intercom == null) ? null : new ServerIntercom(this, intercom);

		synchronized (this.door) {
			this.door.addListener(listener);
		}
	}

	public String getName() {
		remote.checkClosed();
		return name;
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
		return intercom != null;
	}

	public void unlock() {
		synchronized (remote.lock) {
			remote.checkClosed();
			door.unlock();
			fireUnlocked();
		}
	}

	ServerDoor fork() {
		HardwareIntercom intercom = (this.intercom == null) ? null : this.intercom.intercom;
		return new ServerDoor(remote, name, door, intercom);
	}

	// TODO: clear listeners?
	void closeInterface() {
		remote.checkClosed();
		intercom.closeInterface();
	}

}
