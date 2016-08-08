package at.stefl.gatekeeper.server;

import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.audio.AudioFormat;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.inteface.AbstractIntercom;

public class ServerIntercom extends AbstractIntercom {

	final Server server;
	final ServerRemote remote;
	final ServerDoor door;
	final HardwareIntercom intercom;

	public ServerIntercom(ServerDoor door, HardwareIntercom intercom) {
		this.door = door;
		this.remote = door.remote;
		this.server = remote.server;
		this.intercom = intercom;
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
			AudioOutputStream result = intercom.open(microphone);
			fireOpened();
			return result;
		}
	}

	public void close() {
		synchronized (remote.lock) {
			remote.checkClosed();
			server.releaseIntercom(remote, intercom);
			intercom.close();
			fireClosed();
		}
	}

	// TODO: clear listeners?
	void closeInterface() {
		remote.checkClosed();
		close();
	}

}
