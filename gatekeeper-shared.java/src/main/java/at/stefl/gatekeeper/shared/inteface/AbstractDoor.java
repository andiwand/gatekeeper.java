package at.stefl.gatekeeper.shared.inteface;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDoor implements Door {

	private final Set<Listener> listeners;

	public AbstractDoor() {
		this.listeners = new HashSet<Door.Listener>();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	protected void fireBell(Door door) {
		for (Door.Listener listener : listeners) {
			listener.bell(door);
		}
	}

	protected void fireUnlocked(Door door) {
		for (Door.Listener listener : listeners) {
			listener.unlocked(door);
		}
	}

}
