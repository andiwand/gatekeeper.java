package at.stefl.gatekeeper.server.test;

import at.stefl.gatekeeper.server.Server;
import at.stefl.gatekeeper.server.ServerConfig;
import at.stefl.gatekeeper.server.WebSocketService;
import at.stefl.gatekeeper.server.hardware.HardwareDoorFactory;
import at.stefl.gatekeeper.server.hardware.HardwareIntercomFactory;
import at.stefl.gatekeeper.server.hardware.test.EchoIntercomFactory;
import at.stefl.gatekeeper.server.hardware.test.TestDoorFactory;
import at.stefl.gatekeeper.shared.Constants;

public class DesktopServerTest {

	public static void main(String[] args) throws Throwable {
		ServerConfig config = new ServerConfig();
		config.name = "test";
		ServerConfig.Door doorConfig = new ServerConfig.Door();
		doorConfig.name = "front";
		doorConfig.intercom = new ServerConfig.Intercom();
		config.doors.add(doorConfig);

		HardwareDoorFactory doorFactory = new TestDoorFactory();
		HardwareIntercomFactory intercomFactory = new EchoIntercomFactory(Constants.AUDIO_FORMAT);
		Server server = new Server(doorFactory, intercomFactory);
		server.init(config);

		WebSocketService webSocketService = new WebSocketService(server);
		webSocketService.start();
	}

}
