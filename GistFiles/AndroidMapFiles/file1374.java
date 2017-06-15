/**
 * Class to manage all the observables created by the core in order to cache them so there are no
 * duplicated request on the same time, and avoid calls being made too much frequently.
 *
 * The time to live (ttl) for each call can be set in the {@link ObservableManager.Types}
 * <br><br>
 * Created by marcel on 01/06/15.
 */
public class ObservableManager {

  private final EventBus bus;
  private HashMap<Integer, Observable<? extends BaseModel>> obsMap;

// I use event bus to keep all my model in one place so I can get the sticky events from memory. I have a BaseModel.java that keeps
// The result and the created time so Obsevable manager can retreived. Another approach would be to keep the reference of the created time
// here in the ObservableManager.
  public ObservableManager(EventBus bus) {
    this.bus = bus;
    this.obsMap = new HashMap<>();
  }

  public void put(Integer key, Observable<? extends BaseModel> observable) {
    obsMap.put(key, observable);
  }

  public <T extends BaseModel> Observable<T> get(Types type) {
    Object obj = bus.getStickyEvent(type.getType());
    if (obj == null) {
      return (Observable<T>) obsMap.get(type.getKey());
    }
    if (((BaseModel) obj).getCreatedTime() > 0
        && System.currentTimeMillis() - ((BaseModel) obj).getCreatedTime() > type.getTtl()) {
      ((BaseModel) obj).setCreatedTime(0); // This makes sure to invalidate the created time
      obsMap.remove(type.getKey());
      return null;
    }
    return (Observable<T>) obsMap.get(type.getKey());
  }

  public void remove(Integer key) {
    obsMap.remove(key);
  }

  public void clear() {
    obsMap.clear();
  }

  /**
   * Internal class where all types of request are bounded to specific class and a TTL if set
   */
  public enum Types {

    // TODO fill up with every call we need
    STORES(100, StoresList.class, 1000),
    USER(101, User.class),
    ORDER(102, OrdersList.class);

    int key;
    Class type;
    long ttl;

    Types(int key, Class type) {
      this.key = key;
      this.type = type;
    }

    Types(int key, Class type, long ttl) {
      this.key = key;
      this.type = type;
      this.ttl = ttl;
    }

    public int getKey() {
      return key;
    }

    public Class getType() {
      return type;
    }

    public long getTtl() {
      return ttl == 0 ? Globals.DEFAULT_CACHED_TTL : ttl;
    }
  }
}
