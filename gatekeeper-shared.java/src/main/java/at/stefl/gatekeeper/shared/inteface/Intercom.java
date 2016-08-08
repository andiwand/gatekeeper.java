package at.stefl.gatekeeper.shared.inteface;

import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;

public interface Intercom {

	public static interface Listener {
		public void opened(Intercom intercom);

		public void closed(Intercom intercom);
	}

	public static abstract class Adapter implements Listener {
		public void opened(Intercom intercom) {
		}

		public void closed(Intercom intercom) {
		}
	}

	public Door getDoor();

	public boolean isOpen();

	public AudioFormat getMicrophoneFormat();

	public AudioFormat getSpeakerFormat();

	public void addListener(Listener listener);

	public void removeListener(Listener listener);

	public AudioOutputStream open(AudioOutputStream microphone);

	public void close();

}
