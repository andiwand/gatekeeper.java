package at.stefl.gatekeeper.shared.test;

import at.stefl.gatekeeper.shared.network.json.GsonPacketHelper;
import at.stefl.gatekeeper.shared.network.packet.DoorRequest;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;

public class GsonPacketTest {

	public static void main(String[] args) {
		GsonPacketHelper packetHelper = new GsonPacketHelper();

		// serialize packet
		DoorRequest request = new DoorRequest();
		request.setDoor("test");
		StandardPacket packet = new StandardPacket();
		packet.setType(StandardPacket.Type.REQUEST_UNLOCK);
		packet.setPayload(request);

		packet.validate();
		System.out.println(packet.isValid());

		String json = packetHelper.toJson(packet);
		System.out.println(json);

		// deserialize packet
		StandardPacket packet2 = packetHelper.fromJson(json);

		packet2.validate();
		System.out.println(packet2.isValid());

		System.out.println(packet2.getType());
		System.out.println(packet2.getType().getPayloadClass());
		System.out.println(packet2.getPayload().getClass());
	}

}
