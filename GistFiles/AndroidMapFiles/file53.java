@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

	//OMITTED

    GetWSArticles.execute();
    
} // END onCreate


//---------------------------------------------------------------


/**
 * Async task class to get json by making HTTP call
 * */
private class GetWSArticles extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        // Creating service handler class instance
        WSHandler sh = new WSHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, WSHandler.GET);

        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {

//
//                    JSONObject jsonItem = jsonObj.getJSONObject(TAG_VALUES);
//
//                    String id = jsonItem.getString(TAG_ID);
//                    String joke = jsonItem.getString(TAG_JOKE);
//
//                    // tmp hashmap for single joke
//                    HashMap<String, String> jokeHash = new HashMap<String, String>();
//
//                    // adding each child node to HashMap key => value
//                    jokeHash.put(TAG_ID, id);
//                    jokeHash.put(TAG_JOKE, joke);
//
//                    // adding joke to joke list
//                    jokeList.add(jokeHash);
//
//
                //JSONObject jsonObj = new JSONObject(jsonStr);

                articles_json = new JSONArray(jsonStr);

                List<Article> myList = new ArrayList<Article>();

                for (int i = 0; i < articles_json.length(); i++) {

                    JSONObject jsonobj_tmp = articles_json.getJSONObject(i);

                    Article artTemp = new Article();

                    artTemp.setId(Integer.parseInt(jsonobj_tmp.getString("Id")));
                    artTemp.setPostTitle(jsonobj_tmp.getString("PostTitle"));
                    artTemp.setPostBody(jsonobj_tmp.getString("PostBody"));
                    artTemp.setLikeliness(jsonobj_tmp.getString("Likeliness"));
                    artTemp.setTags(jsonobj_tmp.getString("Tags"));
                    artTemp.setPredictionYear(jsonobj_tmp.getString("PredictionYear"));
                    artTemp.setImageUrl("http://api.androidhive.info/music/images/eminem.png");


                }

                // Getting JSON Array node
                //jokes = jsonObj.getJSONArray(TAG_VALUES);

                // looping through All Contacts
                /*for (int i = 0; i < jokes.length(); i++) {
                    JSONObject c = jokes.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    //String category = c.getString(TAG_CATEGORY);
                    String joke = c.getString(TAG_JOKE);

                    // Phone node is JSON Object

                    // tmp hashmap for single contact
                    HashMap<String, String> jokeHash = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    jokeHash.put(TAG_ID, id);
                    jokeHash.put(TAG_JOKE, joke);

                    // adding contact to contact list
                    jokeList.add(jokeHash);
                }*/
            } catch (Exception e) {
                Log.d("tmpArticle", e.toString());
            }
        } else {
            Log.e("WSHandler", "Couldn't get any data from the url");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();
        /**
         * Updating parsed JSON data into ListView
         * */
        //ListAdapter adapter = new SimpleAdapter(
        //        TestHandler.this, jokeList,
         //       R.layout.activity_test_listitem, new String[] { TAG_JOKE }, new int[] { R.id.joke });

        //setListAdapter(adapter);
    }

}  //END AsyncTask