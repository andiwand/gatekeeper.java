package at.stefl.gatekeeper.server;

import java.util.LinkedList;
import java.util.List;

import at.stefl.gatekeeper.shared.audio.AudioFormat;

public class ServerConfig {

	public static class Intercom {
		public String microphone;
		public String speaker;
	}

	public static class Door {
		public String name;
		public Integer bellPin;
		public Integer unlockPin;
		public Intercom intercom;
	}

	public static class Audio {
		public Line microphone;
		public Line speaker;
	}

	public static class Line {
		public AudioFormat audioFormat;
		public Integer bufferTime;
	}

	public static class WebServer {
		public int port;
		public String certificatePath;
		public String rootPath;
	}

	public String name;
	public Audio audio;
	public List<Door> doors = new LinkedList<Door>();
	public WebServer webServer;

}
