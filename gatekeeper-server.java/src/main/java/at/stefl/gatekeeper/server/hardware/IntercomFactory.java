package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.shared.audio.AudioHelper;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class IntercomFactory {

	public static Intercom create(String speaker, String microphone) {
		return new JavaIntercom(AudioHelper.getSourceLineInfo(speaker), AudioHelper.getTargetLineInfo(microphone));
	}

	private IntercomFactory() {
	}

}
