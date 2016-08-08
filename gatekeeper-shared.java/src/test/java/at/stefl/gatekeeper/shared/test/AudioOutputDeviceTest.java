package at.stefl.gatekeeper.shared.test;

import javax.sound.sampled.Line;

import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioHelper;
import at.stefl.gatekeeper.shared.audio.AudioOutputDevice;

public class AudioOutputDeviceTest {

	public static void main(String[] args) throws Throwable {
		Line.Info lineInfo = AudioHelper.getSourceLineInfo("Lautsprecher/Kopfhörer (Realtek High Definition Audio)");
		AudioOutputDevice out = new AudioOutputDevice(lineInfo);
		if (!out.open(Constants.AUDIO_FORMAT, Constants.AUDIO_BUFFER_SIZE)) {
			System.out.println("cannot open");
			return;
		}
		out.start();

		double sampleTime = 1 / Constants.AUDIO_FORMAT.getSampleRate();
		int samples = Constants.AUDIO_BUFFER_SIZE;
		long wait = (long) (1000 * samples * sampleTime);
		byte[] buffer = new byte[samples];

		double frequency = 440;
		double time = 10;

		double t = 0;

		while (t < time) {
			for (int i = 0; i < samples; i++) {
				buffer[i] = (byte) (127 * Math.sin(2 * Math.PI * frequency * t));
				t += sampleTime;
			}

			out.write(buffer, 0, buffer.length);
			Thread.sleep(wait);
		}

		out.close();
	}

}
