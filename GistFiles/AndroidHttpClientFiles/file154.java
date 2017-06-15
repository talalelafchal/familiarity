package com.squaar.comparar;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class MylocalData {
	
	public static Context context;
	
	public JSONObject itemsCategory;
	
	public String strUrl;
	
	public JSONArray arrItems;

    private static MylocalData mInstance= null;
    
    public static synchronized MylocalData getInstance(){

        if(null == mInstance){

                mInstance = new MylocalData();

        }

        return mInstance;
    }
    
    
}