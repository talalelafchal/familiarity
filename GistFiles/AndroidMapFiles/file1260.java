import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

public class BundleDeserializer implements JsonDeserializer<Bundle> {
    public Bundle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (json.isJsonObject()) {
            return handleObject(json.getAsJsonObject(), context);
        } else if (json.isJsonArray()) {
            throw new JsonParseException(BundleDeserializer.class.getSimpleName() + " does not support json as Array");
        } else if (json.isJsonPrimitive()) {
            throw new JsonParseException(BundleDeserializer.class.getSimpleName() + " does not support json as Primitive");
        } else if (json.isJsonNull()) {
            return null;
        } else {
            throw new JsonParseException(BundleDeserializer.class.getSimpleName() + " does not support such kind of json: " + json);
        }
    }

    private void handlePrimitive(Bundle out, String key, JsonPrimitive json) {
        if (json.isBoolean()) {
            out.putBoolean(key, json.getAsBoolean());
        } else if (json.isString()) {
            out.putString(key, json.getAsString());
        } else {
            BigDecimal bigDec = json.getAsBigDecimal();
            // Find out if it is an int type
            try {
                //noinspection ResultOfMethodCallIgnored
                bigDec.toBigIntegerExact();

                try {
                    out.putInt(key, bigDec.intValueExact());
                    return;
                } catch (ArithmeticException e) {
                    // ignore
                }

                out.putLong(key, bigDec.longValue());
                return;
            } catch (ArithmeticException e) {
                // ignore
            }

            // Just return it as a double
            out.putDouble(key, bigDec.doubleValue());
        }
    }

    private Object handleArray(JsonArray json, JsonDeserializationContext context) {
        Object[] array = new Object[json.size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = context.deserialize(json.get(i), Object.class);
        }

        return array;
    }

    private Bundle handleObject(JsonObject json, JsonDeserializationContext context) {
        Bundle map = new Bundle();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonElement v = entry.getValue();
            if (v.isJsonNull()) {
                map.putString(entry.getKey(), null);
            } else if (v.isJsonPrimitive()) {
                handlePrimitive(map, entry.getKey(), v.getAsJsonPrimitive());
            } else if (v.isJsonArray()) {
                // FIXME arrays not supported yet
                map.putBundle(entry.getKey(), Bundle.EMPTY);
            } else {
                map.putBundle(entry.getKey(), handleObject(v.getAsJsonObject(), context));
            }
        }

        return map;
    }
}
