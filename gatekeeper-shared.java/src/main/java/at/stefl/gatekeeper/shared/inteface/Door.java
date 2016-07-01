package at.stefl.gatekeeper.shared.inteface;

public interface Door {

	public static interface Listener {
		public void bell(Door door);

		public void unlocked(Door door);
	}

	public static abstract class Adapter implements Listener {
		public void bell(Door door) {
		}

		public void unlocked(Door door) {
		}
	}

	public String getName();

	public Intercom getIntercom();

	public boolean hasBell();

	public boolean hasUnlock();

	public boolean hasIntercom();

	public void addListener(Listener listener);

	public void removeListener(Listener listener);

	public void unlock();

}
