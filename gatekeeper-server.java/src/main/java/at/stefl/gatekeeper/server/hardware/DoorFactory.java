package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.hardware.raspi.RaspiGpioDoor;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class DoorFactory {

	public static Door create(String name, Integer bellPin, Integer unlockPin, Intercom intercom) {
		return new RaspiGpioDoor(name, bellPin, unlockPin, intercom);
	}

	private DoorFactory() {
	}

}
