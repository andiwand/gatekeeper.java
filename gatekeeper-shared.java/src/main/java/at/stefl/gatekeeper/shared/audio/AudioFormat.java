package at.stefl.gatekeeper.shared.audio;

public class AudioFormat {

	private final float sampleRate;
	private final int sampleSize;
	private final int channels;
	private final boolean signed;
	private final boolean bigEndian;

	public AudioFormat(float sampleRate, int sampleSize, int channels, boolean signed, boolean bigEndian) {
		super();
		this.sampleRate = sampleRate;
		this.sampleSize = sampleSize;
		this.channels = channels;
		this.signed = signed;
		this.bigEndian = bigEndian;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public int getChannels() {
		return channels;
	}

	public boolean isSigned() {
		return signed;
	}

	public boolean isBigEndian() {
		return bigEndian;
	}

	public int calculateBufferSize(double time) {
		return ((int) Math.ceil(sampleSize * sampleRate * time)) * channels;
	}

}
