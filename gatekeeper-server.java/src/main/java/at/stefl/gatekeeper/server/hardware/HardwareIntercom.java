package at.stefl.gatekeeper.server.hardware;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;

import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioHelper;
import at.stefl.gatekeeper.shared.audio.AudioInputDevice;
import at.stefl.gatekeeper.shared.audio.AudioOutputDevice;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.audio.AudioPipe;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class HardwareIntercom implements Intercom {

	private final AudioOutputDevice speaker;
	private final AudioInputDevice microphone;
	private final Set<Listener> listeners;

	private AudioPipe pipe;

	public HardwareIntercom(String speaker, String microphone) {
		this(AudioHelper.getSourceLineInfo(speaker), AudioHelper.getTargetLineInfo(microphone));
	}

	public HardwareIntercom(Line.Info speaker, Line.Info microphone) {
		if (speaker == null)
			throw new NullPointerException();
		if (microphone == null)
			throw new NullPointerException();

		try {
			this.speaker = new AudioOutputDevice(speaker);
			this.microphone = new AudioInputDevice(microphone);
		} catch (LineUnavailableException e) {
			throw new IllegalStateException("cannot initiate lines", e);
		}

		this.listeners = new HashSet<Intercom.Listener>();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public boolean isInit() {
		return speaker.isStarted() & microphone.isStarted();
	}

	public boolean isOpen() {
		return speaker.isStarted() | microphone.isStarted();
	}

	public boolean init(AudioFormat speakerFormat, int speakerBuffer, AudioFormat microphoneFormat,
			int microphoneBuffer) {
		if (!speaker.open(speakerFormat, speakerBuffer)) {
			return false;
		}
		if (!microphone.open(microphoneFormat, microphoneBuffer)) {
			speaker.close();
			return false;
		}
		return true;
	}

	public void destroy() {
		speaker.close();
		microphone.close();
	}

	public AudioOutputStream open(AudioOutputStream microphone) {
		ByteBuffer buffer = ByteBuffer.allocate(this.microphone.getBufferSize());
		pipe = new AudioPipe(this.microphone.getStream(), microphone, buffer);

		this.speaker.start();
		this.microphone.start();
		pipe.start();

		for (Listener listener : listeners) {
			listener.opened(this);
		}

		return this.speaker.getStream();
	}

	public void close() {
		try {
			pipe.interrupt();
			pipe.join();
			pipe = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		speaker.stop();
		microphone.stop();

		for (Listener listener : listeners) {
			listener.closed(this);
		}
	}

}
