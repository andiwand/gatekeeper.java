package at.stefl.gatekeeper.server.http;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class WebServer {

	private class NanoHTTPDImpl extends NanoHTTPD {
		public NanoHTTPDImpl(int port) {
			super(port);
		}

		@Override
		public Response serve(IHTTPSession session) {
			return WebServer.this.serve(session);
		}
	}

	protected final NanoHTTPD httpd;

	public WebServer(int port) {
		this.httpd = new NanoHTTPDImpl(port);
	}

	public void start() throws IOException {
		httpd.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}

	public Response serve(IHTTPSession session) {
		String msg = "<html><body><h1>Hello server</h1>\n";
		Map<String, String> parms = session.getParms();
		if (parms.get("username") == null) {
			msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n"
					+ "</form>\n";
		} else {
			msg += "<p>Hello, " + parms.get("username") + "!</p>";
		}
		return NanoHTTPD.newFixedLengthResponse(msg + "</body></html>\n");
	}

}
