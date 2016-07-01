package at.stefl.gatekeeper.shared.network.packet;

public abstract class Packet<E, T extends Packet.Type<E>> {

	public static interface Type<E> {
		public boolean isValidPayload(E payload);
	}

	private T type;
	private E payload;

	public T getType() {
		return type;
	}

	public E getPayload() {
		return payload;
	}

	public void setType(T type) {
		this.type = type;
	}

	public void setPayload(E payload) {
		this.payload = payload;
	}

	public boolean isValid() {
		if (type == null)
			return false;
		if (!type.isValidPayload(payload))
			return false;
		return true;
	}

	public void validate() {
		if (type == null)
			throw new IllegalStateException("type not set");
		if (!type.isValidPayload(payload))
			throw new IllegalStateException("illegal payload");
	}

}
