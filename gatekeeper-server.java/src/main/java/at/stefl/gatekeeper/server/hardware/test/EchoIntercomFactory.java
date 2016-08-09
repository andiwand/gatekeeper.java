package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.ServerConfig.Intercom;
import at.stefl.gatekeeper.server.hardware.HardwareIntercomFactory;
import at.stefl.gatekeeper.shared.audio.AudioFormat;

public class EchoIntercomFactory implements HardwareIntercomFactory {

	private final AudioFormat audioFormat;

	public EchoIntercomFactory(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
	}

	public EchoIntercom createIntercom(Intercom intercomConfig) {
		EchoIntercom result = new EchoIntercom();
		result.setAudioFormat(audioFormat);
		return result;
	}

}
