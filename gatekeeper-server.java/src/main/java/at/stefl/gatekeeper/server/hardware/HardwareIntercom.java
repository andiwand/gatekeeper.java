package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.shared.inteface.AbstractIntercom;

public abstract class HardwareIntercom extends AbstractIntercom {

	public abstract void init();

	public abstract void destroy();

}
