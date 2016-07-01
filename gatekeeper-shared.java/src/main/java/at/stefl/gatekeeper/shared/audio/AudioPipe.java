package at.stefl.gatekeeper.shared.audio;

import java.nio.ByteBuffer;

// TODO: make reusable
public class AudioPipe extends Thread {

	private final AudioInputStream in;
	private final AudioOutputStream out;
	private final ByteBuffer buffer;
	private boolean run;

	public AudioPipe(AudioInputStream in, AudioOutputStream out, ByteBuffer buffer) {
		this.in = in;
		this.out = out;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		while (run) {
			buffer.clear();
			in.read(buffer);
			buffer.flip();
			out.write(buffer);
		}
	}

	@Override
	public synchronized void start() {
		super.start();
		run = true;
	}

	@Override
	public synchronized void interrupt() {
		run = false;
	}

}