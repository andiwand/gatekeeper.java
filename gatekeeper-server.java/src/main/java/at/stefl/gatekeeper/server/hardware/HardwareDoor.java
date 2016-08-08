package at.stefl.gatekeeper.server.hardware;

import java.util.HashSet;
import java.util.Set;

public abstract class HardwareDoor {

	public static interface Listener {
		public void bell();
	}

	public static abstract class Adapter implements Listener {
		public void bell() {
		}
	}

	private final Set<Listener> listeners;

	public HardwareDoor() {
		this.listeners = new HashSet<HardwareDoor.Listener>();
	}

	public abstract void init();

	public abstract void destory();

	public abstract boolean hasBell();

	public abstract boolean hasUnlock();

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public abstract void unlock();

	protected void fireBell() {
		for (Listener listener : listeners) {
			listener.bell();
		}
	}

}
