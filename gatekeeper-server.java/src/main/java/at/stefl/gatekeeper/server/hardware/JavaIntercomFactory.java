package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.server.ServerConfig.Intercom;
import at.stefl.gatekeeper.shared.audio.AudioHelper;

public class JavaIntercomFactory extends HardwareIntercomFactory {

	@Override
	public HardwareIntercom create(Intercom intercomConfig) {
		return new JavaIntercom(AudioHelper.getSourceLineInfo(intercomConfig.speaker),
				AudioHelper.getTargetLineInfo(intercomConfig.microphone));
	}

}
