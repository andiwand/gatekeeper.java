package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.ServerConfig;
import at.stefl.gatekeeper.shared.audio.AudioHelper;

public class JavaIntercomFactory extends HardwareIntercomFactory {

	@Override
	public HardwareIntercom create(ServerConfig.Intercom intercomConfig) {
		return new JavaIntercom(AudioHelper.getSourceLineInfo(intercomConfig.speaker),
				AudioHelper.getTargetLineInfo(intercomConfig.microphone));
	}

}
