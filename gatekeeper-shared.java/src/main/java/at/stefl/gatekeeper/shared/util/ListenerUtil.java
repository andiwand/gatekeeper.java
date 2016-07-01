package at.stefl.gatekeeper.shared.util;

import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;
import at.stefl.gatekeeper.shared.inteface.Remote;

public class ListenerUtil {

	public static void fireRemoteClosed(Iterable<? extends Remote.Listener> listeners, Remote remote,
			boolean byRemote) {
		for (Remote.Listener listener : listeners) {
			listener.closed(remote, byRemote);
		}
	}

	public static void fireDoorBell(Iterable<? extends Door.Listener> listeners, Door door) {
		for (Door.Listener listener : listeners) {
			listener.bell(door);
		}
	}

	public static void fireDoorUnlocked(Iterable<? extends Door.Listener> listeners, Door door) {
		for (Door.Listener listener : listeners) {
			listener.unlocked(door);
		}
	}

	public static void fireIntercomOpened(Iterable<? extends Intercom.Listener> listeners, Intercom intercom) {
		for (Intercom.Listener listener : listeners) {
			listener.opened(intercom);
		}
	}

	public static void fireIntercomClosed(Iterable<? extends Intercom.Listener> listeners, Intercom intercom) {
		for (Intercom.Listener listener : listeners) {
			listener.closed(intercom);
		}
	}

	private ListenerUtil() {
	}

}
