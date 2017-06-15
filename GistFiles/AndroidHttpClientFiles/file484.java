
/**
 * Created by clint19 on 1/7/2016.
 */
public class cart_product extends Fragment {
    public cart_product() {
    }

    View rootView;
    Function_List lib_function = new Function_List();

    // getting global JONSTags request
    Global_JsonTags jsontags = new Global_JsonTags();
    Global_Variable globalVariable = new Global_Variable();

    MyGridView best_product_gridview;
    ArrayList<HashMap<String, String>> best_prpduct_array = new ArrayList<HashMap<String, String>>();

    ArrayList<HashMap<String, String>> best_prpduct_array_null = new ArrayList<HashMap<String, String>>();

    static int best_scuuess = 0;
    static String category_id;

    View Filter;
    View listview_view_layout;
    View Gridview_view_layout;
    ImageView gridview_button;
    Spinner sortby;
    String price_text;
    String sort_option_value;

    /*ImageLoaderConfiguration config;
    private DisplayImageOptions options;*/
    info.androidhive.slidingmenu.library.ExpandableHeightListView best_product_listview;
    static int best_listview_height = 0;
    ScrollView category_content;
    ProgressBar show_spinner;
    TextView filter_price;

    TextView notworking;
    int abbb = 0;
    // heading catgeoyr_product id best product adapter
    Best_Product mAdapter = new Best_Product(getActivity(), best_prpduct_array);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.category_product_listview, container, false);
        View bestProduct = rootView.findViewById(R.id.best_collection);
        category_content = (ScrollView) rootView.findViewById(R.id.scrollView3);
        show_spinner = (ProgressBar) rootView.findViewById(R.id.progressBar);
        category_content.setVisibility(View.GONE);
        show_spinner.setVisibility(View.VISIBLE);
        notworking = (TextView) rootView.findViewById(R.id.textView11);
        notworking.setVisibility(View.GONE);

        // title of page and go back
        View page_title = rootView.findViewById(R.id.category_name);
        TextView category_title = (TextView) page_title.findViewById(R.id.category_title);
        category_title.setText("Category Name");
        ImageView goback_back_botton = (ImageView) page_title.findViewById(R.id.transaction_back_arrow);

        Delete_Cache delete_cache = new Delete_Cache();
        delete_cache.execute();

     //   clear_cache();

        // go back to previous activity
        goback_back_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("start back event", "back evbent fire");


                onBackPressed();
            }
        });


        best_product_gridview = (MyGridView) bestProduct.findViewById(R.id.gridview11);

        // assiginng value
        category_id = String.valueOf(globalVariable.category_id);
        abbb = lib_function.url_checking_working_not();
        boolean abc = isOnline();
        Log.d("abc-=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=", "" + abc);
        if (!abc) {

            notworking.setText("Internet is not working");
            notworking.setVisibility(View.VISIBLE);
            category_content.setVisibility(View.GONE);


        } else {
            bestproduct_downloading_Task bestproduct_json = new bestproduct_downloading_Task();
            bestproduct_json.execute();
            boolean abc1 = isOnline();

            Log.d("abc-=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=", "" + abc);

            // setting GridViewAdapter
            setAdapter_bestproduct__Task bestproduct_adapter = new setAdapter_bestproduct__Task();
            bestproduct_adapter.execute();


        }


        //-------------------------------------------- sending to product page ----------------------------------------------------------------//
        try {
            // OnClick
            best_product_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    try {
                        Toast.makeText(getActivity(), "position id: " + position, Toast.LENGTH_LONG).show();

                        // gerting  product id
                        globalVariable.product_id = (int) mAdapter.getItemId(position);
                        ((MainActivity) getActivity()).product();

                    } catch (Exception e) {
                        // When Error
                        Log.e("Error", "" + e);
                    }
                }
            });

        } catch (Exception e) {
            Log.d("Error BestPrduct View", "" + e);
        }


        // -----------------buttons for listview , GridView ----------------------------//
        Filter = rootView.findViewById(R.id.category_filtesrsss);
        ImageView listview_view_button = (ImageView) Filter.findViewById(R.id.imageView12);
        gridview_button = (ImageView) Filter.findViewById(R.id.imageView17);
        sortby = (Spinner) Filter.findViewById(R.id.spinner);
        filter_price = (TextView) Filter.findViewById(R.id.textView19);
        final SeekBar mSeekBar = (SeekBar) Filter.findViewById(R.id.seekBar);
        mSeekBar.setProgress(0);
        mSeekBar.incrementProgressBy(50);
        mSeekBar.setMax(1500);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }


            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                price_text = String.valueOf((progress));
                filter_price.setText("$" + price_text + " - " + "$1500");
                /*Filter_Product_Downloading_Task  filter_product_downloading_Task = new Filter_Product_Downloading_Task();
                filter_product_downloading_Task.execute();
*/
                // TODO Auto-generated method stub

            }
        });
        // ----------------- buttons for listview , GridView  --------------------------------//

        // lauyout for listview
        listview_view_layout = rootView.findViewById(R.id.category_best_product_listview);

        // GidView layout
        Gridview_view_layout = rootView.findViewById(R.id.best_collection);
        ProgressBar progressbar = (ProgressBar) Gridview_view_layout.findViewById(R.id.progress);
        progressbar.setVisibility(View.GONE);
        //listview for lsitview category product

        // addding values to spinner
        String[] sort_by_values = {" Default", " Name (A - Z) ", "Name (Z - A)", "Price (Low to High)", " Price (High > Low) ", "Rating (Highest)", " Rating (Lowest) ", "Model (A - Z)", "Model (Z - A)"};
        final String[] sort_order_id = {"default", "name_asc", "name_des", "price_Low_High", "price_High_Low", "rating_highest", "rating_lowest", "model_asc", "model_des"};
        ArrayAdapter<String> gameKindArray = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sort_by_values);
        gameKindArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortby.setAdapter(gameKindArray);

        best_product_listview = (ExpandableHeightListView) listview_view_layout.findViewById(R.id.category_product_listView);
        listview_view_layout.setVisibility(View.GONE);


       /* sortby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "sort_id " + position + "sort value" + sort_order_id[position], Toast.LENGTH_LONG).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        // setting up Adapater for listview
        setAdapter_bestproduct_Listview__Task listview_adapter = new setAdapter_bestproduct_Listview__Task();
        listview_adapter.execute();
        best_product_listview.setExpanded(true);
        //getListViewSize(best_product_listview);

        try {
            // OnClick
            best_product_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    try {
                        //Toast.makeText(getActivity(), "position id: " + position, Toast.LENGTH_LONG).show();

                        // gerting  product id
                        globalVariable.product_id = (int) mAdapter.getItemId(position);
                        ((MainActivity) getActivity()).product();

                    } catch (Exception e) {
                        // When Error
                        Log.e("Error", "" + e);
                    }
                }
            });

        } catch (Exception e) {
            Log.d("Error BestPrduct View", "" + e);
        }


        //--------------------------------------- show listview ----------------------------------------//
        listview_view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //seting listview
                Log.d("ListView", "ListView starts");
                Toast.makeText(getActivity(), "workin on listview", Toast.LENGTH_LONG).show();

                // hiding GridView
                Gridview_view_layout.setVisibility(View.GONE);
                best_product_gridview.setVisibility(View.GONE);


                // show listview
                listview_view_layout.setVisibility(View.VISIBLE);
                best_product_listview.setVisibility(View.VISIBLE);
                // hide progressbar
                ProgressBar progressbar = (ProgressBar) listview_view_layout.findViewById(R.id.progress);
                progressbar.setVisibility(View.GONE);

            }
        });
        //--------------------------------------- show listview ----------------------------------------//

        //--------------------------------------- show Gridview starts ----------------------------------------//
        gridview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //seting listview
                Log.d("ListView", "ListView starts");
                Toast.makeText(getActivity(), "workin on listview", Toast.LENGTH_LONG).show();

                //hiding listiew
                best_product_listview.setVisibility(View.GONE);
                listview_view_layout.setVisibility(View.GONE);

                // show gridview
                Gridview_view_layout.setVisibility(View.VISIBLE);
                best_product_gridview.setVisibility(View.VISIBLE);

            }
        });
        //--------------------------------------- show Gridview End  ----------------------------------------//


        //---------------------------------------------------------- universal loader image library intiliazation ---------------------------------------------//


        //---------------------------------------------------------- universal loader image library intiliazation End---------------------------------------------//
        return rootView;
    }
//--------------------------------------------------------------  Best products Json donwload starts ---------------------------------------------------------//

    private class bestproduct_downloading_Task extends AsyncTask<ArrayList<HashMap<String, String>>, Void, ArrayList<HashMap<String, String>>> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(ArrayList<HashMap<String, String>>... params) {

            try {
                List<NameValuePair> paramssss = new ArrayList<NameValuePair>();
                paramssss.add(new BasicNameValuePair("category_id", category_id));
                JSONArray data = new JSONArray(getJSONUrl(jsontags.Category_Product_JOSN_URLs, paramssss));


                HashMap<String, String> best_product_map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    best_product_map = new HashMap<String, String>();
                    best_product_map.put(jsontags.product_name, c.getString("name"));
                    best_product_map.put(jsontags.product_price, c.getString("price"));
                    best_product_map.put(jsontags.product_image, c.getString("thumb"));
                    best_product_map.put(jsontags.product_description, c.getString("description"));
                    best_product_map.put(jsontags.product_id, c.getString("product_id"));
                    best_product_map.put(jsontags.product_status, c.getString("addtocartstatus"));
                    best_product_map.put(jsontags.product_special_price, c.getString("special"));


                    best_prpduct_array.add(best_product_map);

                    Log.d("prduct", "" + best_prpduct_array);
                }

                best_scuuess = 1;
                Log.d("catgroy suceesss ", "" + best_scuuess);
                Log.d("Best product complete", "Best product complete donwload");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return best_prpduct_array;
        }

        @Override
        protected void onPreExecute() {

            Log.d("starting DialogBox", "starting DialogBox");
            super.onPreExecute();
        }

        // for run mail thread UI
        protected void publishProgress() {


        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }
//--------------------------------------------------------------  Best products Json donwload End ---------------------------------------------------------//


//--------------------------------------------------------------  Best products Adpater calss donwload starts ---------------------------------------------------------//

    private class setAdapter_bestproduct__Task extends AsyncTask<ArrayList<HashMap<String, String>>, Void, ArrayList<HashMap<String, String>>> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(ArrayList<HashMap<String, String>>... params) {
            publishProgress();
            return best_prpduct_array;
        }

        @Override
        protected void onPreExecute() {


            Log.d("starting DialogBox", "starting DialogBox");
            super.onPreExecute();
        }

        // for run mail thread UI
        protected void publishProgress() {


        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

            Log.d("calling Adapter for best product", "best product");


            best_product_gridview.setAdapter(new Best_Product(getActivity(), best_prpduct_array));
            best_product_gridview.setExpanded(true);


            super.onPostExecute(result);
        }
    }
//--------------------------------------------------------------  category products Adpater calss donwload End ---------------------------------------------------------//

    //--------------------------------------------------------------  Category products Adpater calss LIstView  donwload starts ---------------------------------------------------------//

    private class setAdapter_bestproduct_Listview__Task extends AsyncTask<ArrayList<HashMap<String, String>>, Void, ArrayList<HashMap<String, String>>> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(ArrayList<HashMap<String, String>>... params) {
            publishProgress();
            return best_prpduct_array;
        }

        @Override
        protected void onPreExecute() {

            Log.d("starting DialogBox", "starting DialogBox");
            super.onPreExecute();
        }

        // for run mail thread UI
        protected void publishProgress() {


        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

            Log.d("calling Adapter for best product", "best product");


            best_product_listview.setAdapter(new BestProduct_ListView(getActivity(), best_prpduct_array));
            setListViewHeightBasedOnChildren(best_product_listview);
            best_product_listview.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, best_listview_height));

            calling();
            super.onPostExecute(result);
        }
    }


    //--------------------------------------------------------------  Cateory  products Adpater LIstView donwload End ---------------------------------------------------------//

    //------------------------------------------------------------------ geting listview height starts ----------------------------------------------------------------------------//
    public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            // do nothing return null
            return;
        }
        // set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        // setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight
                + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        best_listview_height = totalHeight;
        Log.i("height of listItem:", String.valueOf(totalHeight));


    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        best_listview_height = totalHeight;
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }

    //------------------------------------------------------------------ geting listview height starts -----------------------------//


    public String getJSONUrl(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


    private void calling() {
        int abbb = lib_function.url_checking_working_not();
        if (lib_function.url_checking_working_not() == 200) {
            category_content.setVisibility(View.VISIBLE);
            show_spinner.setVisibility(View.GONE);
        } else {
            notworking.setText("Website Not working" + abbb);
            notworking.setVisibility(View.VISIBLE);
            category_content.setVisibility(View.GONE);
            show_spinner.setVisibility(View.GONE);
        }
    }

    public void onStop() {
        super.onStop();
        onDestroyView();
    }

    public void onDestroyView() {
        super.onDestroyView();
        onDestroy();
    }

    public void onDestroy() {
        super.onDestroy();
        onDetach();
    }

    public void onDetach() {

        super.onDetach();
    }

    // go back to pervious coding
    public void onBackPressed() {
        //Include the code here
        getFragmentManager().popBackStack();
        return;
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        Log.d("internet", "" + netInfo);
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //filter send information to server
    // ------------------------------------------------------ Json Request for prduct information  ---------------------------------------------------------//
    private class Filter_Product_Downloading_Task extends AsyncTask<ArrayList<HashMap<String, String>>, Void, ArrayList<HashMap<String, String>>> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());
        String resultServer;

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(ArrayList<HashMap<String, String>>... params) {
            List<NameValuePair> param_values = new ArrayList<NameValuePair>();
            JSONArray jsonarray = null;
            try {
                best_prpduct_array.clear();

                List<NameValuePair> paramssss = new ArrayList<NameValuePair>();
                paramssss.add(new BasicNameValuePair("category_id", category_id));
                JSONArray data = new JSONArray(getJSONUrl(jsontags.Category_Product_JOSN_URLs, paramssss));
                    Log.d("return data-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-0=-=--" ,""+data );

                HashMap<String, String> best_product_map_filter;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    best_product_map_filter = new HashMap<String, String>();
                    best_product_map_filter.put(jsontags.product_name, c.getString("name"));
                    best_product_map_filter.put(jsontags.product_price, c.getString("price"));
                    best_product_map_filter.put(jsontags.product_image, c.getString("thumb"));
                    best_product_map_filter.put(jsontags.product_description, c.getString("description"));
                    best_product_map_filter.put(jsontags.product_id, c.getString("product_id"));
                    best_product_map_filter.put(jsontags.product_status, c.getString("addtocartstatus"));
                    best_product_map_filter.put(jsontags.product_special_price, c.getString("special"));
                    best_prpduct_array.add(best_product_map_filter);
                    Log.d("prduct", "" + best_prpduct_array);

                }

                best_scuuess = 1;
                Log.d("catgroy suceesss=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-", "" + best_prpduct_array);
                Log.d("Best product complete", "Best product complete donwload");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return best_prpduct_array;
        }

        @Override
        protected void onPreExecute() {

            Log.d("starting DialogBox", "starting DialogBox");
            super.onPreExecute();
        }

        // for run mail thread UI
        protected void publishProgress() {
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            ArrayList<HashMap<String, String>> best_prpduct_array_empty = new ArrayList<HashMap<String, String>>();

            // setting empt  base Adapter
            best_product_gridview.setAdapter(new Best_Product(getActivity(), best_prpduct_array_empty));
            best_product_gridview.setExpanded(true);

            best_product_listview.setAdapter(new Best_Product(getActivity(), best_prpduct_array_empty));


            // setting empt  base Adapter End
            if (best_prpduct_array.size() > 0)
            {
                best_product_gridview.setAdapter(new Best_Product(getActivity(), best_prpduct_array));
                best_product_gridview.setExpanded(true);

                best_product_listview.setAdapter(new BestProduct_ListView(getActivity(), best_prpduct_array));
                setListViewHeightBasedOnChildren(best_product_listview);
                best_product_listview.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, best_listview_height));
            }
            else
            {
            }

        }
        // ------------------------------------------------------ Json Request for prduct information  ---------------------------------------------------------//


    }


    private class Delete_Cache extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

         //   Glide.get(getActivity()).clearDiskCache();
         //   Glide.get(getActivity()).clearMemory();

            return null;
        }

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);


        }
    }

  /*  private void clear_cache() {

        Log.d("image work delete ","clear cache");
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //  imagePipeline.clearMemoryCaches();
        // imagePipeline.clearDiskCaches();

        // combines above two lines
        imagePipeline.clearCaches();
    }*/
}