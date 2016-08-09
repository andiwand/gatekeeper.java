package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.audio.ForwardAudioOutputStream;

public class EchoIntercom extends HardwareIntercom {

	private AudioFormat audioFormat;
	private boolean open;

	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public void setAudioFormat(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
	}

	public boolean isOpen() {
		return open;
	}

	public AudioFormat getMicrophoneFormat() {
		return audioFormat;
	}

	public AudioFormat getSpeakerFormat() {
		return audioFormat;
	}

	public AudioOutputStream open(AudioOutputStream microphone) {
		if (open)
			return null;
		open = true;
		return new ForwardAudioOutputStream(microphone);
	}

	public void close() {
		open = false;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

}
