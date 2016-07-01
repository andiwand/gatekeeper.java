package at.stefl.gatekeeper.server.http;

import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.SecureServerSocketFactory;

public class SecureWebServer extends WebServer {

	public SecureWebServer(int port, KeyStore keyStore) throws IOException {
		super(port);
		httpd.setServerSocketFactory(
				new SecureServerSocketFactory(NanoHTTPD.makeSSLSocketFactory(keyStore, (KeyManager[]) null), null));
	}

}
