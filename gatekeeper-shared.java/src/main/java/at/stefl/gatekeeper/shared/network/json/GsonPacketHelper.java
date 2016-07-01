package at.stefl.gatekeeper.shared.network.json;

import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.stefl.gatekeeper.shared.json.GsonHelper;
import at.stefl.gatekeeper.shared.network.packet.StandardPacket;

public class GsonPacketHelper {

	private static final GsonBuilder BUILDER = createGsonBuilder();

	public static GsonBuilder createGsonBuilder() {
		GsonBuilder builder = GsonHelper.createGsonBuilder();
		builder.registerTypeAdapter(StandardPacket.class, new StandardPacketAdapter());
		return builder;
	}

	public static Gson createGson() {
		return BUILDER.create();
	}

	private final Gson gson;

	public GsonPacketHelper() {
		this.gson = createGson();
	}

	public Gson getGson() {
		return gson;
	}

	public String toJson(StandardPacket packet) {
		return gson.toJson(packet, StandardPacket.class);
	}

	public void toJson(StandardPacket packet, Writer out) {
		gson.toJson(packet, StandardPacket.class, out);
	}

	public StandardPacket fromJson(String json) {
		return gson.fromJson(json, StandardPacket.class);
	}

	public StandardPacket fromJson(Reader in) {
		return gson.fromJson(in, StandardPacket.class);
	}

}
