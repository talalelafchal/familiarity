//this is our main activity where we call service and start all stuff.
package com.generalinexture.proteen.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.generalinexture.proteen.Api.RestApi;
import com.generalinexture.proteen.Dialogs.DialogAsync;
import com.generalinexture.proteen.R;
import com.generalinexture.proteen.Responce.Level4SingleLineResp;
import com.generalinexture.proteen.support.Other;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TapanHP on 9/8/2016.
 */
public class NewClass extends AppCompatActivity {
    private DialogAsync progressDialog;
    private Activity activity;
    private RestApi service;
    private Level4SingleLineResp respLevel4Single;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
        activity=this;
        
        // here  we initialize Retrofit object and build it
        // url is service calling url EX: http://myserver.xyz.com/
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Other.url).addConverterFactory(GsonConverterFactory.create()).build();
        //Object of RestApi class --TODO : See RestApi class for more details
        service = retrofit.create(RestApi.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //call service
        getSingleLineQue();
    }

    private void getSingleLineQue() {
        //here we are calling webservice by sending HashMap parameter type request 
        // you can see it in RestApi class that parameter of calling method is @QueryMap i.e [HashMap] 
        HashMap<String, String> authData = new HashMap<>();
        
        //this method is which via you call WebService and put other parameter same as it.
         authData.put("methodName", "getLevel4IntermediateQuestion");
        progressDialog = new DialogAsync(activity);
        progressDialog.show();
        
        // Level4SingleLineResp is a class which is represent caching response and response will be in it's form
        // TODO : see Level4SingleLineResp class for more detail
        
        // loadLevel4SingleLine() is method inside RestApi class
        // TODO : see RestApi class for more detail
        Call<Level4SingleLineResp> call = service.loadLevel4SingleLine(authData);
        call.enqueue(new Callback<Level4SingleLineResp>() {
            @Override
            public void onResponse(Call<Level4SingleLineResp> call, Response<Level4SingleLineResp> response) {
                // checkStatus method is for cheking basics of response which is static method at class level
                if (Other.checkStatus(activity, response.isSuccessful(), response.body())) {
                    progressDialog.dismiss();
                    //here we caught successful response
                    respLevel4Single = response.body();
                    // and now we can get all related parameters using it like this
                    respLevel4Single.getNoOfAttemptedQuestions();
                    respLevel4Single.getSufficientPoint();
                  
                   } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Level4SingleLineResp> call, Throwable t) {
                Log.e("failure", "" + t.getLocalizedMessage());
                progressDialog.dismiss();
                Other.openDialogerrorMsg(getString(R.string.server_responce_not_readable), activity);
            }
        });

    }

}

