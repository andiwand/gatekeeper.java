package at.stefl.gatekeeper.server.test;

import at.stefl.gatekeeper.server.hardware.JavaIntercom;
import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioHelper;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.audio.ForwardAudioOutputStream;

public class HardwareIntercomTest {

	public static void main(String[] args) throws Throwable {
		String speakerName = "Lautsprecher/Kopfhörer (Realtek High Definition Audio)";
		String microphoneName = "Mikrofon (Realtek High Definiti";

		JavaIntercom intercom = new JavaIntercom(AudioHelper.getSourceLineInfo(speakerName),
				AudioHelper.getTargetLineInfo(microphoneName));

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
