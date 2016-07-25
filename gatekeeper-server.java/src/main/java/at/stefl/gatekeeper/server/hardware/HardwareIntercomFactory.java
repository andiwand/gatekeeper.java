package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.ServerConfig;

public abstract class HardwareIntercomFactory {

	public abstract HardwareIntercom create(ServerConfig.Intercom intercomConfig);

}
