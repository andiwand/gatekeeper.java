package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;

// TODO: implement echo
public class TestIntercom extends HardwareIntercom {

	private AudioFormat audioFormat;

	public TestIntercom() {
	}

	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public void setAudioFormat(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
	}

	public boolean isOpen() {
		return false;
	}

	public HardwareDoor getDoor() {
		// TODO: implement
		return null;
	}

	public AudioFormat getMicrophoneFormat() {
		return audioFormat;
	}

	public AudioFormat getSpeakerFormat() {
		return audioFormat;
	}

	public AudioOutputStream open(AudioOutputStream microphone) {
		return null;
	}

	public void close() {

	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

}
