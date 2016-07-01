package at.stefl.gatekeeper.shared;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import at.stefl.gatekeeper.shared.audio.AudioFormat;

public class Constants {

	public static final Charset CHARSET = Charset.forName("utf-8");

	public static final String WEBSOCKET_SCHEME = "ws";
	public static final int WEBSOCKET_PORT = 12345;

	public static final int BROADCAST_PORT = 12345;
	public static final Inet4Address BROADCAST_4_ADDRESS;
	public static final int BROADCAST_BUFFER = 64 * 1024;
	public static final int BEACON_INTERVAL = 5000;

	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100, 1, 1, true, true);
	public static final double AUDIO_BUFFER_TIME = 0.1;
	public static final int AUDIO_BUFFER_SIZE = 4096; //AUDIO_FORMAT.calculateBufferSize(AUDIO_BUFFER_TIME);

	static {
		try {
			BROADCAST_4_ADDRESS = (Inet4Address) Inet4Address
					.getByAddress(new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255 });
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

	private Constants() {
	}

}
