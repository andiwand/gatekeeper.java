package at.stefl.gatekeeper.server.hardware.raspi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;

public class RaspiGpioDoor extends HardwareDoor {

	private final String name;
	private final HardwareIntercom intercom;
	private final Integer bellPin;
	private final Integer unlockPin;
	private final long unlockDuration;

	private GpioController gpio;
	private GpioPinDigitalInput bell;
	private GpioPinDigitalOutput unlock;

	private final GpioPinListenerDigital bellListener = new GpioPinListenerDigital() {
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
			// TODO: consume events within specific time slice
			bell();
		}
	};

	public RaspiGpioDoor(String name, Integer bellPin, Integer unlockPin, long unlockDuration,
			HardwareIntercom intercom) {
		this.name = name;
		this.intercom = intercom;
		this.bellPin = bellPin;
		this.unlockPin = unlockPin;
		this.unlockDuration = unlockDuration;
	}

	public void init() {
		gpio = GpioFactory.getInstance();
		if (bellPin == null) {
			bell = null;
		} else {
			bell = gpio.provisionDigitalInputPin(RaspiPinHelper.getPin(bellPin), "bell", PinPullResistance.PULL_UP);
			bell.addListener(bellListener);
		}
		if (unlockPin != null) {
			unlock = gpio.provisionDigitalOutputPin(RaspiPinHelper.getPin(unlockPin), "unlock", PinState.HIGH);
		}
	}

	public void destory() {
		this.gpio.shutdown();
	}

	public String getName() {
		return name;
	}

	public HardwareIntercom getIntercom() {
		return intercom;
	}

	public boolean hasBell() {
		return bellPin != null;
	}

	public boolean hasUnlock() {
		return unlockPin != null;
	}

	public boolean hasIntercom() {
		return intercom != null;
	}

	public void bell() {
		fireBell();
	}

	public void unlock() {
		unlock.pulse(unlockDuration, false);
		fireUnlocked();
	}

}
