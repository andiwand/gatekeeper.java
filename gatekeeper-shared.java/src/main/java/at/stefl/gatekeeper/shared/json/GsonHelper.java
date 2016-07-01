package at.stefl.gatekeeper.shared.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

	private static final GsonBuilder BUILDER = createGsonBuilder();

	public static GsonBuilder createGsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		return builder;
	}

	public static Gson createGson() {
		return BUILDER.create();
	}

	private GsonHelper() {
	}

}
