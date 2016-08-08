package at.stefl.gatekeeper.server.hardware.test;

import at.stefl.gatekeeper.server.hardware.HardwareDoor;

public class TestDoor extends HardwareDoor {

	public boolean hasBell() {
		return true;
	}

	public boolean hasUnlock() {
		return true;
	}

	public void unlock() {
		System.out.println("door unlocked");
	}

	public void ring() {
		System.out.println("door ringed");
		fireBell();
	}

	@Override
	public void init() {
	}

	@Override
	public void destory() {
	}

}
