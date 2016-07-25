package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.ServerConfig;

public abstract class HardwareDoorFactory {

	public abstract HardwareDoor create(ServerConfig.Door doorConfig);

}
