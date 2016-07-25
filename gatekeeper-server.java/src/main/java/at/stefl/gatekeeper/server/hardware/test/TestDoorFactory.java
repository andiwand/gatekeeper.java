package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.ServerConfig.Door;
import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareDoorFactory;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.server.hardware.HardwareIntercomFactory;

public class TestDoorFactory extends HardwareDoorFactory {

	private final HardwareIntercomFactory intercomFactory;

	public TestDoorFactory() {
		this(null);
	}

	public TestDoorFactory(HardwareIntercomFactory intercomFactory) {
		this.intercomFactory = intercomFactory;
	}

	@Override
	public HardwareDoor create(Door doorConfig) {
		HardwareIntercom intercom = null;
		if (intercomFactory != null)
			intercom = intercomFactory.create(doorConfig.intercom);
		return new TestDoor(doorConfig.name, intercom);
	}

}
