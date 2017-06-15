package com.example.w.convinentshopping.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.convinentshopping.utils.Constants;
import com.convinentshopping.utils.Product;
import com.example.w.convinentshopping.Dailog.ItemDailog;
import com.convenientshopping.Adapters.ListAdapter;
import com.example.w.convinentshopping.LoginActivity;
import com.example.w.convinentshopping.R;
import com.example.w.convinentshopping.Session;
import com.example.w.convinentshopping.camera;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFragment extends Fragment implements ItemDailog.DailogProduct,ListAdapter.ProductDelete{


    Button btnScanNow;
    Button btnDone;
    TextView totalPrice;
    RequestQueue requestQueue;

    String jsonResponse = "";

    RecyclerView recyclerView;
    ListAdapter adapter;

    private String listurl, saveListUrl;
    private List<Integer> productids;
    public static final int DIALOG_FRAGMENT = 1;
    private static final String TAG = "ListFragment";

    public ListFragment() {
        listurl = Constants.getAbsUrl("list");
        saveListUrl = Constants.getAbsUrl("addList");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productids = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        btnScanNow = (Button) view.findViewById(R.id.btnScanNow);
        btnDone = (Button) view.findViewById(R.id.done);
        totalPrice = (TextView) view.findViewById(R.id.totaltxt);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);


        initRecycleview();


        btnScanNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),camera.class);
                startActivityForResult(intent,0);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Getting order ready!");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                List<Product> products = adapter.getList();

                if(products!=null){

                    final String json = new Gson().toJson(products);

                    Log.d(TAG, "onDoneClick: "+json);

                    Session session = new Session(getActivity());

                    //final JSONArray jsonArray;
                    try {
                        //jsonArray = new JSONArray(json);
                        JSONObject jsonData = new JSONObject();
                        jsonData.put("jsonData",json);
                        jsonData.put("total_price",totalPrice.getText().toString());
                        jsonData.put("customer_id",session.getUserID());
                        //jsonObject.put("current_date",new Date());

                        Log.d(TAG, "onDoneClick: "+jsonData);


                        if (jsonData!=null){
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, saveListUrl,
                                            jsonData, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (response.optBoolean("success")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "List Saved! Proceed to Cashier", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "List Didn't saved Let's try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Server Error", Toast.LENGTH_LONG).show();
                                }
                            });

//                            try {
//                                Map<String, String> headers = jsonObjectRequest.getHeaders();
//                                for (Map.Entry<String, String> entry: headers.entrySet()) {
//                                    Log.d(TAG, entry.getKey() + ": " + entry.getValue());
//                                }
//                            } catch (AuthFailureError authFailureError) {
//                                authFailureError.printStackTrace();
//                            }
                            requestQueue.add(jsonObjectRequest);
                        }
                        else {
                            Toast.makeText(getActivity(), "Cart List is empty!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }


    public void initRecycleview(){
        adapter = new ListAdapter(ListFragment.this,productids,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }


    public void Request(final String num) {

        if(num != null){
            StringRequest strReq = new StringRequest(Request.Method.POST, listurl,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObject;
                            try {

                                jsonObject = new JSONObject(response);

                                String Name = jsonObject.getString("Name");
                                String Price = jsonObject.getString("Price");
                                int productid = jsonObject.getInt("Product_Id");

                                if(!productids.contains(productid))
                                {
                                    Log.d(TAG, "onResponse: "+productid);
                                    Log.d(TAG, "onResponse: "+Name);
                                    Log.d(TAG, "onResponse: "+Price);

                                    ItemDailog itemDailog = ItemDailog.newInstance(Name,Price,productid,null);
                                    itemDailog.setDailogProduct(ListFragment.this);
                                    itemDailog.show(getActivity().getSupportFragmentManager(),"itemdailog");

                                    jsonResponse += "Name: " + Name + "\n\n";
                                    jsonResponse += "Price: " + Price + "\n\n";

                                }
                                else {
                                    Toast.makeText(getActivity(),"Product already added", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Internet Connection Error", Toast.LENGTH_LONG).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Barcode", num);
                    return params;
                }
            };
            requestQueue.add(strReq);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 0)
        {
            if(resultCode == CommonStatusCodes.SUCCESS)
            {
                if(data!=null)
                {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    String number = barcode.displayValue;

                    if(number!=null) {
                        Log.d(TAG, " barcode found "+number);
                        Request(number);
                    }else {
                        Log.d(TAG, "No barcode found ");
                    }
                }
                else
                {
                    Log.d(TAG, "No barcode found ");
                }
            }
        }

        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onProductAdded(Product product,int productid) {

        if(product!=null) {

            productids.add(productid);
            adapter.addItem(product);
            evaulatePrice(adapter.getList());
        }
    }

    @Override
    public void onProductCancel() {
    }

    @Override
    public void evaulatePrice(List<Product> products) {
        int mtotalPrice = 0;

        for (int i = 0; i < products.size(); i++) {
            mtotalPrice += (int)(Double.valueOf(products.get(i).price) * Double.valueOf(products.get(i).Qty));
        }
        totalPrice.setText(String.valueOf(mtotalPrice));
    }

    @Override
    public void onProductDeleted() {
        evaulatePrice(adapter.getList());
    }
}