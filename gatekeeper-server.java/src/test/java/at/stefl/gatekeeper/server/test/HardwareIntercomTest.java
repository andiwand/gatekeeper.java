package at.stefl.gatekeeper.server.test;

import at.stefl.gatekeeper.server.hardware.HardwareIntercom;
import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.audio.ForwardAudioOutputStream;

public class HardwareIntercomTest {

	public static void main(String[] args) throws Throwable {
		String speakerName = "Lautsprecher/Kopfhörer (Realtek High Definition Audio)";
		String microphoneName = "Mikrofon (Realtek High Definiti";

		HardwareIntercom intercom = new HardwareIntercom(speakerName, microphoneName);

		intercom.init(Constants.AUDIO_FORMAT, Constants.AUDIO_BUFFER_SIZE, Constants.AUDIO_FORMAT,
				Constants.AUDIO_BUFFER_SIZE);

		ForwardAudioOutputStream forward = new ForwardAudioOutputStream();
		AudioOutputStream speaker = intercom.open(forward);
		forward.setOutput(speaker);

		System.out.println("wait...");
		Thread.sleep(5000);

		System.out.println("close...");
		intercom.close();
		System.out.println("closed");
	}

}
