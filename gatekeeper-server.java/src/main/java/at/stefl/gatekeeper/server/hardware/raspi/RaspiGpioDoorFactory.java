package at.stefl.gatekeeper.server.hardware.raspi;

import at.stefl.gatekeeper.server.ServerConfig;
import at.stefl.gatekeeper.server.hardware.HardwareDoorFactory;

public class RaspiGpioDoorFactory implements HardwareDoorFactory {

	public RaspiGpioDoor createDoor(ServerConfig.Door doorConfig) {
		return new RaspiGpioDoor(doorConfig.bellPin, doorConfig.unlockPin, doorConfig.unlockDuration);
	}

}
