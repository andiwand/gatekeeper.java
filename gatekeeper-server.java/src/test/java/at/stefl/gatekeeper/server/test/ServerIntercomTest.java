package at.stefl.gatekeeper.server.test;

import java.nio.ByteBuffer;
import java.util.Collection;

import at.stefl.gatekeeper.server.Server;
import at.stefl.gatekeeper.server.ServerConfig;
import at.stefl.gatekeeper.shared.Constants;
import at.stefl.gatekeeper.shared.audio.AudioOutputStream;
import at.stefl.gatekeeper.shared.inteface.Door;
import at.stefl.gatekeeper.shared.inteface.Intercom;

public class ServerIntercomTest {

	public static void main(String[] args) throws Throwable {
		ServerConfig config = new ServerConfig();
		config.name = "test";
		ServerConfig.Door doorConfig = new ServerConfig.Door();
		doorConfig.name = "front";
		doorConfig.intercom = new ServerConfig.Intercom();
		doorConfig.intercom.speaker = "Lautsprecher/Kopfhörer (Realtek High Definition Audio)";
		doorConfig.intercom.microphone = "Mikrofon (Realtek High Definiti";
		config.doors.add(doorConfig);

		Server server = new Server();
		server.init(config);

		Collection<? extends Door> doors = server.getDoors();
		Door door = doors.iterator().next();
		System.out.println(door);

		door.unlock();

		Intercom intercom = door.getIntercom();
		AudioOutputStream out = intercom.open(null);

		double sampleTime = 1 / Constants.AUDIO_FORMAT.getSampleRate();
		int samples = Constants.AUDIO_BUFFER_SIZE;
		long wait = (long) (1000 * samples * sampleTime);
		ByteBuffer buffer = ByteBuffer.allocate(samples);

		double frequency = 440;
		double time = 10;

		double t = 0;

		while (t < time) {
			buffer.clear();

			for (int i = 0; i < samples; i++, t += sampleTime) {
				double value = Math.sin(2 * Math.PI * frequency * t);
				buffer.put((byte) (127 * value));
			}

			buffer.rewind();
			out.write(buffer);
			Thread.sleep(wait);
		}

		intercom.close();
	}

}
