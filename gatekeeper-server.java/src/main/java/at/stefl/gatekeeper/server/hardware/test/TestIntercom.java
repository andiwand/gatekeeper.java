package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;

// TODO: implement echo
public class TestIntercom extends HardwareIntercom {

	public TestIntercom() {
	}

	public boolean isOpen() {
		return false;
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
