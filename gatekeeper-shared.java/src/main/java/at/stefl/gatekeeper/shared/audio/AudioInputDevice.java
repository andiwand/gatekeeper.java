package at.stefl.gatekeeper.shared.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioInputDevice {

	private class Stream extends AudioInputStream {
		private final byte[] buffer;

		public Stream() {
			this.buffer = new byte[line.getBufferSize()];
		}

		@Override
		public int read(ByteBuffer bytes) {
			int result;

			if (bytes.hasArray()) {
				result = line.read(bytes.array(), bytes.arrayOffset(), bytes.limit());
				bytes.position(bytes.position() + result);
			} else {
				result = Math.min(buffer.length, bytes.remaining());
				if (result > 0) {
					result = line.read(buffer, 0, result);
					bytes.put(buffer, 0, result);
				}
			}

			return result;
		}
	}

	private final TargetDataLine line;
	private final Stream stream;

	public AudioInputDevice(Line.Info info) throws LineUnavailableException {
		this.line = (TargetDataLine) AudioSystem.getLine(info);
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
			return true;
		} catch (LineUnavailableException e) {
			return false;
		}
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

	public int read(byte[] b, int off, int len) {
		return line.read(b, off, len);
	}

}
