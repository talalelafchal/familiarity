public class TaskWS extends AsyncTask<Void, Void, JSONObject> {
    Activity mActivity;

    public TaskWS(Activity activity) {
        super();

        this.mActivity = activity;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        return readProducts();
    }

    private JSONObject readProducts() {
        HttpClientFactory factory = new HttpClientFactory();
        JSONObject result = factory.sendGet(Extras.BASE_PRODUCTS);
        
        return result;
    }
}