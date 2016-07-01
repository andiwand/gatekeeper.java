package at.stefl.gatekeeper.shared.network.packet;

public class DoorSignal {

	public static enum Type {
		BELL, UNLOCKED, TOOK_OFF, HUNG_UP;
	}

	private String door;
	private Type signalType;

	public String getDoor() {
		return door;
	}

	public Type getSignalType() {
		return signalType;
	}

	public void setDoor(String door) {
		this.door = door;
	}

	public void setSignalType(Type signalType) {
		this.signalType = signalType;
	}

}
