import android.content.Intent;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

/**
 * Provides a way to wrap a serializable {@link Map} in order to preserve its class
 * during serialization inside an {@link Intent}, otherwise it would be "flattened"
 * in a {@link android.os.Parcel} and unparceled as a {@link java.util.HashMap}.
 */
public class MapWrapper<T extends Map & Serializable> implements Serializable {
    
    private final T map;

    public MapWrapper(@NonNull T map) {
        this.map = map;
    }

    public T getMap() {
        return map;
    }

    /**
     * Add extra map data to the intent. The name must include a package prefix, for example
     * the app com.android.contacts would use names like "com.android.contacts.ShowAll".
     * <p>
     * The provided map will be wrapped to preserve its class during serialization.
     * Use {@link #getMapExtra(Intent, String)} to deserialize it.
     *
     * @param intent The intent to add data to.
     * @param name   The name of the extra data, with package prefix.
     * @param map    The map data value.
     *
     * @return The same {@link Intent} object, for chaining multiple calls into a single statement.
     *
     * @see Intent#putExtra(String, Serializable)
     */
    @NonNull
    public static <T extends Map & Serializable> Intent putMapExtra(
            @NonNull Intent intent, @NonNull String name, @NonNull T map) {
        return intent.putExtra(name, new MapWrapper<>(map));
    }

    /**
     * Retrieve extra map data from the intent.
     *
     * @param intent The intent to retrieve data from.
     * @param name   The name of the desired extra item.
     *
     * @return The value of an extra map item that was previously added with
     * {@link #putMapExtra(Intent, String, Map)} or {@code null} if no data was found.
     *
     * @throws ClassCastException
     * If the {@link Serializable} object with the specified name is not of type
     * {@link MapWrapper} or if the wrapped {@code Map} is not of type {@link T}.
     * 
     * @see Intent#getSerializableExtra(String)
     */
    @Nullable
    public static <T extends Map & Serializable> T getMapExtra(
            @NonNull Intent intent, @NonNull String name)
            throws ClassCastException {
        final Serializable s = intent.getSerializableExtra(name);
        //noinspection unchecked
        return s == null ? null : ((MapWrapper<T>)s).getMap();
    }
    
}
