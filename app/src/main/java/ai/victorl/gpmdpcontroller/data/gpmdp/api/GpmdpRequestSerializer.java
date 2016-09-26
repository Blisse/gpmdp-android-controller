package ai.victorl.gpmdpcontroller.data.gpmdp.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class GpmdpRequestSerializer implements JsonSerializer<GpmdpRequest> {
    @Override
    public JsonElement serialize(GpmdpRequest src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("namespace", src.namespace);
        jsonObject.addProperty("method", src.method);
        if (src.callback != null) {
            jsonObject.addProperty("requestID", src.requestId);
        }
        jsonObject.add("arguments", context.serialize(src.arguments));
        return jsonObject;
    }
}
