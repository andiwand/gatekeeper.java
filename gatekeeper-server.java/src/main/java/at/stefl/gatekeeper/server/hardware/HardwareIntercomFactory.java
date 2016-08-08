package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.ServerConfig;

public interface HardwareIntercomFactory {

	public HardwareIntercom createIntercom(ServerConfig.Intercom intercomConfig);

}
