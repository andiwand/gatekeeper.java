package at.stefl.gatekeeper.shared.inteface;

import java.util.HashSet;
import java.util.Set;

// TODO: check closed
// TODO: synchronize listeners
// TODO: apply to other abstract classes
// TODO: super interface?
public abstract class AbstractRemote implements Remote {

	private final Set<Listener> listeners;

	public AbstractRemote() {
		this.listeners = new HashSet<Listener>();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	protected void fireClosed(Remote remote, boolean byRemote) {
		for (Remote.Listener listener : listeners) {
			listener.closed(remote, byRemote);
		}
	}

}
