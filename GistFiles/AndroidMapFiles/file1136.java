import java.io.Serializable;

/**
 * DL Cache Value is a data object that holds data to be stored in Session or SharedPreference.
 * All cache objects in Session or SharedPreference must be of type DLCacheValue.
 *
 * @author Damon
 */
@SuppressWarnings("unused")
public class DLCacheValue implements Serializable {
    private static final long serialVersionUID = 2164077113668197178L;

    private String _key;
    private Object _obj;
    private long _timeStamp;
    //if time for expiry is less than 0
    //then no need to check expiry
    private long _timeExpiry;
    private boolean _flagPersistence;

    private String _constraint;

    private DLCacheValue(String key, Object obj, boolean flagPersistence) {
        this(key, obj, 0, flagPersistence);
    }

    /**
     * Creates new DLCacheValue object with following parameters.
     *
     * @param key             is the key of that object value
     * @param obj             is the object
     * @param timeExpiry      expiry time of object
     * @param flagPersistence tells if object should be saved in
     *                        SharedPreference (persistent memory)
     * @param constraint      namespace or pool of DLCacheValue
     */
    public DLCacheValue(String key, Object obj,
                        long timeExpiry, boolean flagPersistence, String constraint) {
        this._key = key;
        this._obj = obj;
        this._flagPersistence = flagPersistence;
        this._timeExpiry = timeExpiry;
        this._timeStamp = System.currentTimeMillis();
        this._constraint = constraint;
    }

    /**
     * Creates new DLCacheValue object with following parameters.
     *
     * @param key             is the key of that object value
     * @param obj             is the object
     * @param timeExpiry      expiry time of object
     * @param flagPersistence tells if object should be saved in
     *                        SharedPreference (persistent memory)
     */
    public DLCacheValue(String key, Object obj,
                        long timeExpiry, boolean flagPersistence) {
        this(key, obj, timeExpiry, flagPersistence, null);
    }

    /**
     * check the cache value whether it is expired.
     *
     * @return true if object is expired
     */
    public boolean isExpired() {
        if (_timeExpiry > 0) {
            long now = System.currentTimeMillis();
            return (now - _timeStamp > _timeExpiry);
        } else {
            return false;
        }
    }

    /**
     * Verify if object belongs to the right constraint.
     *
     * @param constraint namespace or pool of DLCacheValue
     * @return true if object is valid
     */
    public boolean isValid(String constraint) {
        if(_constraint == null){
            return true;
        }
        if (this._constraint == null && constraint == null) {
            return true;
        }
        if (constraint == null) {
            return false;
        }
        return (this._constraint.equals(constraint));
    }

    /**
     * @return DLCacheValue object
     */
    public Object getObj() {
        return _obj;
    }

    @Override
    public String toString() {
        return "\nDLCacheValue{" +
                "\n\t_key='" + _key + '\'' +
                ", \n\t_obj=" + _obj +
                ", \n\t_timeStamp=" + _timeStamp +
                ", \n\t_timeExpiry=" + _timeExpiry +
                ", \n\t_flagPersistence=" + _flagPersistence +
                ", \n\t_constraint='" + _constraint + '\'' +
                "\n}";
    }
}
