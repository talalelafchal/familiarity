
    public static final class CachedPref<T> {
        final static Map<Class<?>, PrefConverter<?>> PREF_CONVERTER_MAP = new HashMap<>();
        public static void addConverter(Class<?> type , PrefConverter<?> converter) {
            PREF_CONVERTER_MAP.put(type, converter);
        }

        Class<T> type;
        private String key;
        T defaultValue;


        CachedPref(String key, T defaultValue, Class<T> type) {
            this.type = type;
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public void set(T value) {
            if (mPref == null) {
                throw new IllegalStateException(Pref.class.getSimpleName() +
                        " is not initialized, call initializeInstance(..) method first.");
            }

            if (type == Boolean.class) {
                mPref.edit().putBoolean(key, (Boolean) value).apply();
            } else if (type == Float.class) {
                mPref.edit().putFloat(key, (Float) value).apply();
            } else if (type == String.class) {
                mPref.edit().putString(key, (String) value).apply();
            } else if (type == Integer.class) {
                mPref.edit().putInt(key, (Integer) value).apply();
            } else if (type == Long.class) {
                mPref.edit().putLong(key, (Long) value).apply();
            } else if ( PREF_CONVERTER_MAP.containsKey(type) ) {
                PrefConverter<T> converter = (PrefConverter<T>) PREF_CONVERTER_MAP.get(type);
                mPref.edit().putString(key, converter.convertToString(value));
            } else {
                throw new IllegalArgumentException();
            }
        }

        public T get() {
            if (mPref == null) {
                throw new IllegalStateException(Pref.class.getSimpleName() +
                        " is not initialized, call initializeInstance(..) method first.");
            }

            Object value;
            if (type == Boolean.class) {
                value = mPref.getBoolean(key, (Boolean) defaultValue);
            } else if (type == Float.class) {
                value = mPref.getFloat(key, (Float) defaultValue);
            } else if (type == String.class) {
                value = mPref.getString(key, (String) defaultValue);
            } else if (type == Integer.class) {
                value =  mPref.getInt(key, (Integer) defaultValue);
            } else if (type == Long.class) {
                value = mPref.getLong(key, (Long) defaultValue);
            } else if ( PREF_CONVERTER_MAP.containsKey(type) ) {
                PrefConverter<T> converter = (PrefConverter<T>) PREF_CONVERTER_MAP.get(type);
                value = converter.valueOf(mPref.getString(key, (String) defaultValue));
            } else {
                throw new IllegalArgumentException();
            }
            return type.cast(value);
        }

    }