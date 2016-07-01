package at.stefl.gatekeeper.shared.audio;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

public class AudioHelper {

	public static List<Mixer.Info> getMixerInfos() {
		return getMixerInfos(true);
	}

	public static List<Mixer.Info> getMixerInfos(boolean filter) {
		List<Mixer.Info> mixers = new LinkedList<Mixer.Info>();

		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(info);

			if (filter && !validMixer(mixer))
				continue;

			mixers.add(info);
		}

		return mixers;
	}

	public static List<Mixer.Info> getOutputMixerInfos() {
		return getOutputMixerInfos(true);
	}

	public static List<Mixer.Info> getOutputMixerInfos(boolean filter) {
		List<Mixer.Info> mixers = new LinkedList<Mixer.Info>();

		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(info);

			if (mixer.getSourceLineInfo().length == 0)
				continue;
			if (filter && !validOutput(mixer))
				continue;

			mixers.add(info);
		}

		return mixers;
	}

	public static List<Mixer.Info> getInputMixerInfos() {
		return getInputMixerInfos(true);
	}

	public static List<Mixer.Info> getInputMixerInfos(boolean filter) {
		List<Mixer.Info> mixers = new LinkedList<Mixer.Info>();

		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(info);

			if (mixer.getTargetLineInfo().length == 0)
				continue;
			if (filter && !validInput(mixer))
				continue;

			mixers.add(info);
		}

		return mixers;
	}

	public static boolean validLine(Mixer mixer, Line.Info info) {
		try {
			Line line = mixer.getLine(info);
			line.open();
			line.close();
		} catch (LineUnavailableException e) {
			return false;
		}

		if (!(info instanceof DataLine.Info))
			return false;
		AudioFormat[] formats = ((DataLine.Info) info).getFormats();
		return formats.length > 0;
	}

	public static boolean validInput(Mixer mixer) {
		for (Line.Info info : mixer.getTargetLineInfo()) {
			if (validLine(mixer, info))
				return true;
		}
		return false;
	}

	public static boolean validOutput(Mixer mixer) {
		for (Line.Info info : mixer.getSourceLineInfo()) {
			if (validLine(mixer, info))
				return true;
		}
		return false;
	}

	public static boolean validMixer(Mixer mixer) {
		return validInput(mixer) || validOutput(mixer);
	}

	public static Mixer.Info getMixerInfo(String name) {
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			if (info.getName().equals(name))
				return info;
		}

		return null;
	}

	public static Line.Info getTargetLineInfo(String mixerName) {
		Mixer.Info info = getMixerInfo(mixerName);
		Mixer mixer = AudioSystem.getMixer(info);
		return getTargetLineInfo(mixer);
	}

	public static Line.Info getTargetLineInfo(Mixer mixer) {
		Line.Info[] lineInfos = mixer.getTargetLineInfo();
		if (lineInfos.length <= 0)
			return null;

		return lineInfos[0];
	}

	public static Line.Info getSourceLineInfo(String mixerName) {
		Mixer.Info info = getMixerInfo(mixerName);
		Mixer mixer = AudioSystem.getMixer(info);
		return getSourceLineInfo(mixer);
	}

	public static Line.Info getSourceLineInfo(Mixer mixer) {
		Line.Info[] lineInfos = mixer.getSourceLineInfo();
		if (lineInfos.length <= 0)
			return null;

		return lineInfos[0];
	}

	private AudioHelper() {
	}

}
