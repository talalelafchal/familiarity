public class GsonTransformer implements Transformer{
    public <T> T transform(String url, Class<T> type, String encoding, byte[] data, AjaxStatus status) {                    
        Gson gson = new Gson();
        return gson.fromJson(new String(data), type);
    }
}