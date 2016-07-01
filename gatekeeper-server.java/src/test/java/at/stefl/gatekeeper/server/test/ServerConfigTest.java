package at.stefl.gatekeeper.server.test;

import com.google.gson.Gson;

import at.stefl.gatekeeper.server.ServerConfig;

public class ServerConfigTest {

	public static void main(String[] args) {
		ServerConfig config = new ServerConfig();
		config.name = "test";
		ServerConfig.Door door = new ServerConfig.Door();
		door.name = "front";
		door.intercom = new ServerConfig.Intercom();
		door.intercom.speaker = "a";
		door.intercom.microphone = "b";
		config.doors.add(door);

		Gson gson = new Gson();
		System.out.println(gson.toJson(config));
	}

}
