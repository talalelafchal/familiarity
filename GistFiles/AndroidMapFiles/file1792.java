import android.support.v4.util.SimpleArrayMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Chris Jenkins on 13/03/2014.
 */
public class SimpleArrayMapJsonSerializer implements JsonSerializer<SimpleArrayMap> {

    public static final Type TYPE = new TypeToken<SimpleArrayMap>() {
    }.getType();

    @Override
    public JsonElement serialize(final SimpleArrayMap src, final Type typeOfSrc, final JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        final JsonObject jsonObject = new JsonObject();
        final int length = src.size();
        Object k, v;
        for (int i = 0; i < length; i++) {
            k = src.keyAt(i);
            v = src.valueAt(i);
            jsonObject.add(String.valueOf(k), context.serialize(v));
        }
        return jsonObject;
    }
}
