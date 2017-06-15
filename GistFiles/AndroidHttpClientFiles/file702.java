
package com.example.wertalp.sensortester.net;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class GameConnector {

    HttpClient client;
    HttpPost   post  ;
    StringEntity se  ;

    JSONObject Parent;
    JSONArray jsonGameMoves ;
    List<GameMove> datastreamList ;

    public GameConnector(String url) throws UnsupportedEncodingException {

        this.client = new DefaultHttpClient();
        this.post   = new HttpPost(url);



    StringEntity se = new StringEntity( Parent.toString()) ;
    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    post.setHeader("Accept", "application/json");
    post.setHeader("Content-type", "application/json");
    post.setEntity(se);

    }

    public void makeJSONData() throws JSONException {

        this.Parent = new JSONObject();
        JSONArray jsonGameMoves = new JSONArray();

        for (int i = 0 ; i < datastreamList.size() ; i++)
        {
            JSONObject jsonObj = new JSONObject();

            jsonObj.put("id", datastreamList.get(i).getId());
            jsonObj.put("current_value", datastreamList.get(i).getCurrent_value());
            jsonGameMoves.put(jsonObj);
        }
       // Parent.put("datastreamList", jsonGameMoves);
        //Parent.put("version", version);


    }



    public void SENDDATA () throws IOException {

        this.client.execute(post);
    }


     class GameMove {

            String id;
            String current_value; // or int

           public String getId() {
               return id;
           }

           public void setId(String id) {
               this.id = id;
           }

           public String getCurrent_value() {
               return current_value;
           }

           public void setCurrent_value(String current_value) {
               this.current_value = current_value;
           }

           //get
//set
        }

    }
