package at.stefl.gatekeeper.server.test;

import at.stefl.gatekeeper.server.Server;
import at.stefl.gatekeeper.server.ServerConfig;
import at.stefl.gatekeeper.server.WebSocketService;

public class ServerTest {

	public static void main(String[] args) throws Throwable {
		ServerConfig config = new ServerConfig();
		config.name = "test";
		ServerConfig.Door doorConfig = new ServerConfig.Door();
		doorConfig.name = "front";
//		doorConfig.bellPin = 0;
//		doorConfig.unlockPin = 1;
		doorConfig.intercom = new ServerConfig.Intercom();
		doorConfig.intercom.speaker = "Lautsprecher/Kopfhörer (Realtek High Definition Audio)";
		doorConfig.intercom.microphone = "Mikrofon (Realtek High Definiti";
		config.doors.add(doorConfig);

		Server server = new Server();
		server.init(config);

		WebSocketService webSocketService = new WebSocketService(server);
		webSocketService.start();
	}

}
