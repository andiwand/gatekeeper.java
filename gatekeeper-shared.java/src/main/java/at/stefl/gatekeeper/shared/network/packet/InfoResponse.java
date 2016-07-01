package at.stefl.gatekeeper.shared.network.packet;

import java.util.Map;

public class InfoResponse {

	public static class Door {
		public boolean hasBell;
		public boolean hasUnlock;
		public boolean hasIntercom;
	}

	public String name;
	public Map<String, Door> doors;

}
