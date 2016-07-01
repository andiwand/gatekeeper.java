package at.stefl.gatekeeper.shared.audio;

import java.nio.ByteBuffer;

public abstract class AudioInputStream {

	public abstract int read(ByteBuffer bytes);

}
