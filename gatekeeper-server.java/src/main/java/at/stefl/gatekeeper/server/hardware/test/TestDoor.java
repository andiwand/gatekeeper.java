package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;
import at.stefl.gatekeeper.server.hardware.HardwareIntercom;

public class TestDoor extends HardwareDoor {

	private final String name;
	private final HardwareIntercom intercom;

	public TestDoor(String name, HardwareIntercom intercom) {
		this.name = name;
		this.intercom = intercom;
	}

	public String getName() {
		return name;
	}

	public boolean hasBell() {
		return true;
	}

	public boolean hasUnlock() {
		return true;
	}

	public boolean hasIntercom() {
		return intercom != null;
	}

	public void unlock() {
		System.out.println("door " + name + " unlocked");
		fireUnlocked();
	}

	public void ring() {
		System.out.println("door " + name + " ringed");
		fireBell();
	}

	@Override
	public void init() {
	}

	@Override
	public void destory() {
	}

	@Override
	public HardwareIntercom getIntercom() {
		return intercom;
	}

}
