package at.stefl.gatekeeper.server;

import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.inteface.AbstractIntercom;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class ServerIntercom extends AbstractIntercom {

	final Server server;
	final ServerRemote remote;
	final ServerDoor door;
	final HardwareIntercom intercom;

	private final Intercom.Listener listener = new Intercom.Listener() {
		public void opened(Intercom intercom) {
			fireOpened(intercom);
		}

		public void closed(Intercom intercom) {
			fireClosed(intercom);
		}
	};

	public ServerIntercom(ServerDoor door, HardwareIntercom intercom) {
		this.door = door;
		this.remote = door.remote;
		this.server = remote.server;
		this.intercom = intercom;

		synchronized (this.intercom) {
			this.intercom.addListener(listener);
		}
	}

	public boolean isOpen() {
		remote.checkClosed();
		return intercom.isOpen();
	}

	public ServerDoor getDoor() {
		return door;
	}

	public AudioFormat getMicrophoneFormat() {
		remote.checkClosed();
		return intercom.getMicrophoneFormat();
	}

	public AudioFormat getSpeakerFormat() {
		remote.checkClosed();
		return intercom.getSpeakerFormat();
	}

	public AudioOutputStream open(AudioOutputStream microphone) {
		synchronized (remote.lock) {
			remote.checkClosed();
			server.reserveIntercom(remote, intercom);
			// TODO: handle unsuccessful open
			return intercom.open(microphone);
		}
	}

	public void close() {
		synchronized (remote.lock) {
			remote.checkClosed();
			server.releaseIntercom(remote, intercom);
			intercom.close();
		}
	}

	// TODO: clear listeners?
	void closeInterface() {
		remote.checkClosed();
		close();
	}

}
