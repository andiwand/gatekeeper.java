package at.stefl.gatekeeper.shared.inteface;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractIntercom implements Intercom {

	private final Set<Listener> listeners;

	public AbstractIntercom() {
		listeners = new HashSet<Listener>();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	protected void fireOpened(Intercom intercom) {
		for (Intercom.Listener listener : listeners) {
			listener.opened(intercom);
		}
	}

	protected void fireClosed(Intercom intercom) {
		for (Intercom.Listener listener : listeners) {
			listener.closed(intercom);
		}
	}

}
