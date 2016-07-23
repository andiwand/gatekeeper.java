package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.hardware.raspi.RaspiGpioDoor;

public class HardwareDoorFactory {

	public static HardwareDoor create(String name, Integer bellPin, Integer unlockPin, long unlockDuration,
			HardwareIntercom intercom) {
		return new RaspiGpioDoor(name, bellPin, unlockPin, unlockDuration, intercom);
	}

	private HardwareDoorFactory() {
	}

}
