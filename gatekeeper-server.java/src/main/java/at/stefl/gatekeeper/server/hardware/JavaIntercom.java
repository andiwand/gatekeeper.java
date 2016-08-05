package at.stefl.gatekeeper.server.hardware;

import java.nio.ByteBuffer;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;

import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioInputDevice;
import at.stefl.gatekeeper.shared.audio.AudioOutputDevice;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.audio.AudioPipe;

public class JavaIntercom extends HardwareIntercom {

	private final AudioOutputDevice speaker;
	private final AudioInputDevice microphone;

	private AudioFormat format;
	private int buffer;

	private AudioPipe pipe;

	public JavaIntercom(Line.Info speaker, Line.Info microphone) {
		if (speaker == null)
			throw new NullPointerException();
		if (microphone == null)
			throw new NullPointerException();

		this.format = Constants.AUDIO_FORMAT;
		this.buffer = Constants.AUDIO_BUFFER_SIZE;

		try {
			this.speaker = new AudioOutputDevice(speaker);
			this.microphone = new AudioInputDevice(microphone);
		} catch (LineUnavailableException e) {
			throw new IllegalStateException("line unavailable", e);
		}
	}

	public AudioFormat getFormat() {
		return format;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	public boolean isInit() {
		return speaker.isOpen() & microphone.isOpen();
	}

	public boolean isOpen() {
		return speaker.isStarted() | microphone.isStarted();
	}

	public HardwareDoor getDoor() {
		// TODO: implement
		return null;
	}

	public AudioFormat getMicrophoneFormat() {
		return format;
	}

	public AudioFormat getSpeakerFormat() {
		return format;
	}

	public void init() {
		speaker.open(format, buffer);
		microphone.open(format, buffer);
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

		fireOpened(this);

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

		// TODO: flush

		speaker.stop();
		microphone.stop();

		fireClosed(this);
	}

}
