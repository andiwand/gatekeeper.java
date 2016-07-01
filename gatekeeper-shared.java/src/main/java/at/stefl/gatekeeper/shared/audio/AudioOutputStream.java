package at.stefl.gatekeeper.shared.audio;

import java.nio.ByteBuffer;

public abstract class AudioOutputStream {

	public abstract void write(ByteBuffer bytes);

}
