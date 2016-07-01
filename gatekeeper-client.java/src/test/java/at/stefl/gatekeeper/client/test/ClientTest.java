package at.stefl.gatekeeper.client.test;

import at.stefl.gatekeeper.client.WebSocketRemote;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.audio.ForwardAudioOutputStream;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class ClientTest {

	public static void main(String[] args) throws Throwable {
		WebSocketRemote remote = new WebSocketRemote();
		remote.connect("localhost");

		System.out.println(remote.getName());
		System.out.println(remote.getDoors());

		Door door = remote.getDoor("front");
		System.out.println(door.getName());
		System.out.println(door.hasUnlock());
		door.unlock();

		Intercom intercom = door.getIntercom();
		System.out.println(intercom.isOpen());
		ForwardAudioOutputStream forward = new ForwardAudioOutputStream();
		AudioOutputStream speaker = intercom.open(forward);
		forward.setOutput(speaker);
		System.out.println(intercom.isOpen());
		Thread.sleep(5000);
		intercom.close();
		System.out.println(intercom.isOpen());
	}

}
