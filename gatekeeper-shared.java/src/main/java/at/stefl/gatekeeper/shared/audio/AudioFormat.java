package at.stefl.gatekeeper.shared.audio;

public class AudioFormat {

	private final float sampleRate;
	private final int sampleSize;
	private final int channels;
	private final boolean signed;
	private final boolean bigEndian;
	private final boolean floating;

	public AudioFormat(float sampleRate, int sampleSize, int channels, boolean signed, boolean bigEndian) {
		this(sampleRate, sampleSize, channels, signed, bigEndian, false);
	}

	public AudioFormat(float sampleRate, int sampleSize, int channels, boolean signed, boolean bigEndian,
			boolean floating) {
		this.sampleRate = sampleRate;
		this.sampleSize = sampleSize;
		this.channels = channels;
		this.signed = signed;
		this.bigEndian = bigEndian;
		this.floating = floating;
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

	public boolean isFloating() {
		return floating;
	}

	public int calculateBufferSize(double time) {
		return ((int) Math.ceil(sampleSize * sampleRate * time)) * channels;
	}

}
