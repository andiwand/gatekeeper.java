package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.ServerConfig.Door;
import at.stefl.gatekeeper.server.hardware.HardwareDoorFactory;

public class TestDoorFactory implements HardwareDoorFactory {

	public TestDoor createDoor(Door doorConfig) {
		return new TestDoor();
	}

}
