package at.stefl.gatekeeper.shared.inteface;

import java.util.Collection;

public interface Remote {

	public static interface Listener {
		public void closed(Remote remote, boolean byRemote);
	}

	public static abstract class Adapter implements Listener {
		public void closed(Remote remote, boolean byRemote) {
		}
	}

	public void addListener(Listener listener);

	public void removeListener(Listener listener);

	public String getName();

	public Collection<? extends Door> getDoors();

	public Door getDoor(String door);

	public void close();

}
