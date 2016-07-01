package at.stefl.gatekeeper.server.hardware;

import java.util.HashSet;
import java.util.Set;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import at.stefl.gatekeeper.shared.inteface.Door;

public class HardwareDoor implements Door {

	private final String name;
	private final HardwareIntercom intercom;

	private final GpioController gpio;
	private final GpioPinDigitalInput bellPin;
	private final GpioPinDigitalOutput unlockPin;

	private final Set<Listener> listeners;

	private final GpioPinListenerDigital bellListener = new GpioPinListenerDigital() {
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
			// TODO: consume events within specific time slice
			bell();
		}
	};

	public HardwareDoor(String name, Integer bellPin, Integer unlockPin, HardwareIntercom intercom) {
		this.name = name;
		this.intercom = intercom;
		
		this.gpio = null;

//		this.gpio = GpioFactory.getInstance();
		if (bellPin == null) {
			this.bellPin = null;
		} else {
			this.bellPin = gpio.provisionDigitalInputPin(RaspiPinHelper.getPin(bellPin), "bell",
					PinPullResistance.PULL_UP);
			this.bellPin.addListener(bellListener);
		}
		this.unlockPin = (unlockPin == null) ? null
				: gpio.provisionDigitalOutputPin(RaspiPinHelper.getPin(unlockPin), "unlock", PinState.HIGH);

		this.listeners = new HashSet<Listener>();
	}

	public void init() {
		// TODO: init gpio controller
	}

	public void destroy() {
		this.gpio.shutdown();
	}

	public String getName() {
		return name;
	}

	public HardwareIntercom getIntercom() {
		return intercom;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public boolean hasBell() {
		return bellPin != null;
	}

	public boolean hasUnlock() {
		return true;//return unlockPin != null;
	}

	public boolean hasIntercom() {
		return intercom != null;
	}

	public void bell() {
		for (Listener listener : listeners) {
			listener.bell(this);
		}
	}

	public void unlock() {
		System.out.println("unlock " + name);

		for (Listener listener : listeners) {
			listener.unlocked(this);
		}
	}

}
