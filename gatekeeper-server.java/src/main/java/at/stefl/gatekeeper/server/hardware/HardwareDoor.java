package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.shared.inteface.AbstractDoor;

public abstract class HardwareDoor extends AbstractDoor {

	public abstract void init();

	public abstract void destory();

	public abstract HardwareIntercom getIntercom();

}
