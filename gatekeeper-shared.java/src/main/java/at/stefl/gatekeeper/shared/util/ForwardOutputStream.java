package at.stefl.gatekeeper.shared.util;

import java.io.IOException;
import java.io.OutputStream;

public class ForwardOutputStream extends OutputStream {

	private OutputStream out;

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		if (out == null)
			return;
		out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (out == null)
			return;
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (out == null)
			return;
		out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		if (out == null)
			return;
		out.flush();
	}

}