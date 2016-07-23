package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.shared.audio.AudioHelper;

public class HardwareIntercomFactory {

	public static HardwareIntercom create(String speaker, String microphone) {
		return new JavaIntercom(AudioHelper.getSourceLineInfo(speaker), AudioHelper.getTargetLineInfo(microphone));
	}

	private HardwareIntercomFactory() {
	}

}
