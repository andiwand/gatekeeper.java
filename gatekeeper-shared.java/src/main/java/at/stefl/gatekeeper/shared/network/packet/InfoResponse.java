package at.stefl.gatekeeper.shared.network.packet;

import java.util.Map;

import at.stefl.gatekeeper.shared.audio.AudioFormat;

public class InfoResponse {

	public static class Door {
		public boolean hasBell;
		public boolean hasUnlock;
		public Intercom intercom;
	}

	public static class Intercom {
		public AudioFormat microphone;
		public AudioFormat speaker;
	}

	public String name;
	public Map<String, Door> doors;

}
