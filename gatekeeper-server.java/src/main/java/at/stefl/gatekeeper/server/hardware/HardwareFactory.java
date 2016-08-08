package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.ServerConfig.Door;
import at.stefl.gatekeeper.server.ServerConfig.Intercom;

public class HardwareFactory implements HardwareDoorFactory, HardwareIntercomFactory {

	private final HardwareDoorFactory doorFactory;
	private final HardwareIntercomFactory intercomFactory;

	public HardwareFactory(HardwareDoorFactory doorFactory, HardwareIntercomFactory intercomFactory) {
		this.doorFactory = doorFactory;
		this.intercomFactory = intercomFactory;
	}

	public HardwareDoor createDoor(Door doorConfig) {
		return doorFactory.createDoor(doorConfig);
	}

	public HardwareIntercom createIntercom(Intercom intercomConfig) {
		return intercomFactory.createIntercom(intercomConfig);
	}

}
