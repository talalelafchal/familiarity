import java.util.Collection;
import java.util.Map;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    @SuppressWarnings("rawtypes")
    public static Object getObject(Map map, Object key) {
        return getObject(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Object getObject(Map map, Object key, Object defaultValue) {
        if (map != null) {
            Object value = map.get(key);

            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }
}
