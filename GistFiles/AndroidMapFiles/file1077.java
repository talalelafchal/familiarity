public class SessionData {
    private final HashMap<String,String> data = new HashMap<String,String>();

    // METHODS para acessar data
    public String get(String key) {
        return data.get(key);
    }

    public static void put(String key, String value){
         data.put(key,value);
    }

    public static void remove(String key){
        data.remove(key);
    }
}

public class SessionUtils {
    private static SessionData sessionData = null;

    public static SessionData getSessionData() {
        if (sessionData == null) {
            SessionUtils.sessionData = new SessionData();
        }
        return SessionUtils.sessionData
    }
    public static void clearSessionData() {
        SessionUtils.sessionData = null;
    }
}

/*

SessionData session = SessionUtils.getSessionData()
session.put("key", "blablabla");
session.put("key2", "22");

*/