package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.ServerConfig;

public interface HardwareDoorFactory {

	public HardwareDoor createDoor(ServerConfig.Door doorConfig);

}
