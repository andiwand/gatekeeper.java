package at.stefl.gatekeeper.shared.test;

import at.stefl.gatekeeper.shared.audio.AudioHelper;

public class AudioHelperTest {
	
	public static void main(String[] args) {
		System.out.println(AudioHelper.getMixerInfos());
		System.out.println(AudioHelper.getInputMixerInfos());
		System.out.println(AudioHelper.getOutputMixerInfos());
	}

}
