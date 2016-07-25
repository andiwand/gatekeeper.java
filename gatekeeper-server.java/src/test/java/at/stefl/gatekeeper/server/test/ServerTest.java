package at.stefl.gatekeeper.server.test;

import at.stefl.gatekeeper.server.Server;
import at.stefl.gatekeeper.server.ServerConfig;
import at.stefl.gatekeeper.server.WebSocketService;
import at.stefl.gatekeeper.server.hardware.HardwareDoorFactory;
import at.stefl.gatekeeper.server.hardware.HardwareIntercomFactory;
import at.stefl.gatekeeper.server.hardware.JavaIntercomFactory;
import at.stefl.gatekeeper.server.hardware.raspi.RaspiGpioDoorFactory;

public class ServerTest {

	public static void main(String[] args) throws Throwable {
		ServerConfig config = new ServerConfig();
		config.name = "test";
		ServerConfig.Door doorConfig = new ServerConfig.Door();
		doorConfig.name = "front";
		// doorConfig.bellPin = 0;
		// doorConfig.unlockPin = 1;
		doorConfig.intercom = new ServerConfig.Intercom();
		doorConfig.intercom.speaker = "Lautsprecher/Kopfh�rer (Realtek High Definition Audio)";
		doorConfig.intercom.microphone = "Mikrofon (Realtek High Definiti";
		config.doors.add(doorConfig);

		HardwareIntercomFactory intercomFactory = new JavaIntercomFactory();
		HardwareDoorFactory doorFactory = new RaspiGpioDoorFactory(intercomFactory);
		Server server = new Server(doorFactory);
		server.init(config);

		WebSocketService webSocketService = new WebSocketService(server);
		webSocketService.start();
	}

}
