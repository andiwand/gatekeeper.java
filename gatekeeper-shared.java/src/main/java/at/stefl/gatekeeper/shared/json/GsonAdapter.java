package at.stefl.gatekeeper.shared.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public abstract class GsonAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

}
