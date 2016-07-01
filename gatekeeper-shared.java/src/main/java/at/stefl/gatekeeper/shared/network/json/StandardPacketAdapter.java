package at.stefl.gatekeeper.shared.network.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import at.stefl.gatekeeper.shared.json.GsonAdapter;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;

public class StandardPacketAdapter extends GsonAdapter<StandardPacket> {

	private static final String PROPERTY_TYPE = "type";
	private static final String PROPERTY_PAYLOAD = "data";

	public StandardPacket deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonElement typeElement = json.getAsJsonObject().get(PROPERTY_TYPE);
		StandardPacket.Type type = context.deserialize(typeElement, StandardPacket.Type.class);
		Object payload = null;

		if (type.hasPayload()) {
			JsonElement payloadElement = json.getAsJsonObject().get(PROPERTY_PAYLOAD);
			payload = context.deserialize(payloadElement, type.getPayloadClass());
		}

		StandardPacket result = new StandardPacket();
		result.setType(type);
		result.setPayload(payload);
		return result;
	}

	public JsonElement serialize(StandardPacket src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		JsonElement typeElement = context.serialize(src.getType(), StandardPacket.Type.class);
		result.add(PROPERTY_TYPE, typeElement);

		if (src.getType().hasPayload()) {
			JsonElement payloadElement = context.serialize(src.getPayload(), src.getType().getPayloadClass());
			result.add(PROPERTY_PAYLOAD, payloadElement);
		}

		return result;
	}

}
