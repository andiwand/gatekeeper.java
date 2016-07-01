package at.stefl.gatekeeper.shared.util;

import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayOutputStream extends OutputStream {

	private final byte[] array;
	private final int offset;
	private final int length;
	private int size;

	public ByteArrayOutputStream(byte[] array) {
		this(array, 0, array.length);
	}

	public ByteArrayOutputStream(byte[] array, int offset, int length) {
		this.array = array;
		this.offset = offset;
		this.length = length;
	}

	public byte[] getArray() {
		return array;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void write(int b) throws IOException {
		if (size >= length)
			throw new IllegalStateException("buffer overflow");
		array[size++] = (byte) b;
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (size + len > length)
			throw new IllegalStateException("buffer overflow");
		System.arraycopy(b, off, array, size, len);
		size += len;
	}

	public void reset() {
		size = 0;
	}

}
