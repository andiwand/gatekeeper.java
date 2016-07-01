package at.stefl.gatekeeper.shared.network.packet;

public class StandardPacket extends Packet<Object, StandardPacket.Type> {

	public static enum Type implements Packet.Type<Object> {
		SIGNAL_BEACON(BeaconSignal.class),
		SIGNAL_DOOR(DoorSignal.class),

		CLOSE_INTERCOM(null),

		REQUEST_INFO(null),
		REQUEST_UNLOCK(DoorRequest.class),
		REQUEST_INTERCOM(DoorRequest.class),

		RESPONSE_INFO(InfoResponse.class),
		RESPONSE_SIMPLE(SimpleResponse.class);

		private final Class<?> payloadClass;

		private Type(Class<?> payloadClass) {
			this.payloadClass = payloadClass;
		}

		public Class<?> getPayloadClass() {
			return payloadClass;
		}

		public boolean hasPayload() {
			return payloadClass != null;
		}

		public boolean isValidPayload(Object payload) {
			if (payloadClass == null)
				return payload == null;
			return (payload != null) && payloadClass.equals(payload.getClass());
		}
	}

}
