package at.stefl.gatekeeper.shared.audio;

import java.nio.ByteBuffer;

public class ForwardAudioOutputStream extends AudioOutputStream {

	private AudioOutputStream out;

	public ForwardAudioOutputStream() {
	}

	public ForwardAudioOutputStream(AudioOutputStream out) {
		this.out = out;
	}

	public void setOutput(AudioOutputStream out) {
		this.out = out;
	}

	@Override
	public void write(ByteBuffer bytes) {
		if (out == null)
			return;
		this.out.write(bytes);
	}

}