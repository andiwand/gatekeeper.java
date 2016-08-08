package at.stefl.gatekeeper.server.hardware;

import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;

public abstract class HardwareIntercom {

	public abstract void init();

	public abstract void destroy();

	public abstract boolean isOpen();

	public abstract AudioFormat getMicrophoneFormat();

	public abstract AudioFormat getSpeakerFormat();

	public abstract AudioOutputStream open(AudioOutputStream microphone);

	public abstract void close();

}
