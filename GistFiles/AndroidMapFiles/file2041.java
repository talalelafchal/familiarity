public class CustomPostRequest extends Request<String> {
    . 
    . 
    private Map<String, String> mParams;
    . 
    . 
    public void SetPostParam(String strParam, String strValue)
    { 
        mParams.put(strParam, strValue);
    } 
 
    @Override 
    public Map<String, String> getParams() {
        return mParams;
    } 
 
    @Override 
    public String getCacheKey() {
        String temp = super.getCacheKey();
        for (Map.Entry<String, String> entry : mParams.entrySet())
            temp += entry.getKey() + "=" + entry.getValue();
        return temp;
    } 
} 