import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class TypeSafeSharedPreferences implements SharedPreferences {

    private final SharedPreferences sharedPreferences;

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean getBoolean(String key, boolean defValue) {
        try {
            return sharedPreferences.getBoolean(key, defValue);
        }catch(ClassCastException e) {
            return toBoolean(getAll().get(key));
        }
    }

    public long getLong(String key, long defValue) {
        try {
            return sharedPreferences.getLong(key, defValue);
        }catch(ClassCastException e) {
            return toLong(getAll().get(key));
        }
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public float getFloat(String key, float defValue) {
        try{
            return sharedPreferences.getFloat(key, defValue);
        }catch(ClassCastException e) {
            return (float) toDouble(getAll().get(key));
        }
    }

    public int getInt(String key, int defValue) {
        try{
            return sharedPreferences.getInt(key, defValue);
        }catch(ClassCastException e) {
            return toInteger(getAll().get(key));
        }
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public SharedPreferences.Editor edit() {
        return sharedPreferences.edit();
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    public String getString(String key, String defValue) {
        try {
            return sharedPreferences.getString(key, defValue);
        }catch(ClassCastException e) {
            return String.valueOf(getAll().get(key));
        }
    }

    public TypeSafeSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    static boolean toBoolean(Object value) throws ClassCastException {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            String stringValue = (String) value;
            if ("true".equalsIgnoreCase(stringValue)) {
                return true;
            } else if ("false".equalsIgnoreCase(stringValue)) {
                return false;
            }
        }

        try {
            return toDouble(value) != 0;
        }catch(ClassCastException e) {
            throw classCastException(value, "boolean");
        }
    }

    static double toDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        throw classCastException(value, "double");
    }

    static int toInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return (int) Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        throw classCastException(value, "integer");
    }

    static long toLong(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return (long) Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        throw classCastException(value, "long");
    }

    static String toString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return String.valueOf(value);
        }
        throw classCastException(value, "string");
    }

    private static ClassCastException classCastException(Object value, String type) {
        return new ClassCastException(value + " cannot be casted to " + type);
    }
}
