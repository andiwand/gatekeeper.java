package at.stefl.gatekeeper.shared.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioOutputDevice {

	private class Stream extends AudioOutputStream {
		private final byte[] buffer;

		public Stream() {
			this.buffer = new byte[line.getBufferSize()];
		}

		@Override
		public void write(ByteBuffer bytes) {
			if (bytes.hasArray()) {
				line.write(bytes.array(), bytes.arrayOffset(), bytes.limit());
			} else {
				while (true) {
					int len = Math.min(buffer.length, bytes.remaining());
					if (len <= 0)
						break;
					bytes.get(buffer, 0, len);
					line.write(buffer, 0, len);
				}
			}
		}
	}

	private final SourceDataLine line;
	private final Stream stream;

	public AudioOutputDevice(Line.Info info) throws LineUnavailableException {
		this.line = (SourceDataLine) AudioSystem.getLine(info);
		this.stream = new Stream();
	}

	public boolean isOpen() {
		return line.isOpen();
	}

	public boolean isStarted() {
		return line.isActive();
	}

	public Stream getStream() {
		return stream;
	}

	public int getBufferSize() {
		return line.getBufferSize();
	}

	public boolean open(AudioFormat format, int buffer) {
		try {
			line.open(new javax.sound.sampled.AudioFormat(format.getSampleRate(), format.getSampleSize() * 8,
					format.getChannels(), format.isSigned(), format.isBigEndian()), buffer);
		} catch (LineUnavailableException e) {
			return false;
		}

		return true;
	}

	public void close() {
		line.close();
	}

	public void start() {
		line.start();
	}

	public void stop() {
		line.stop();
	}

	public int write(byte[] b, int off, int len) {
		return line.write(b, off, len);
	}

}
