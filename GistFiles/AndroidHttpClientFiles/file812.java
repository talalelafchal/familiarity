import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.commons.io.FileUtils;
import org.json.*;
import android.util.Base64;
import android.util.Log;

public class Uploader {

    private String url;
    private String fileName;

    public Uploader(String url, String fileName){
        this.url = url;
        this.fileName = fileName;
    }

    public Boolean upload() throws JSONException, ClientProtocolException, IOException {
        Boolean success = true;
        JSONObject jsonObject = constructPictureJson();
            DefaultHttpClient httpClient = new DefaultHttpClient();

            ResponseHandler <String> responseHandler = new BasicResponseHandler();
            HttpPost postMethod = new HttpPost(url);
            postMethod.setEntity(new StringEntity(jsonObject.toString()));
            postMethod.setHeader("Accept", "application/json");
            postMethod.setHeader("Content-type", "application/json");
            postMethod.setHeader("Data-type", "json");
            try{
            httpClient.execute(postMethod, responseHandler);
            } catch (org.apache.http.client.HttpResponseException error){
                Log.d("Uploader Class Error", "Error code: "+error.getStatusCode());
                Log.d("Uploader Class Error", "Error message: "+error.getMessage());
                success = false;
            }
            //Log.d("server resposne", response);
            return success;
    }

    public JSONObject constructPictureJson() throws JSONException, IOException{
        String userId = "1"; 
        String folderId = "1";
        String[] file = fileName.split("/");
        JSONObject pictureData = new JSONObject();
        pictureData.put("user_id", userId);
        pictureData.put("folder_id", folderId); 
        pictureData.put("picture_name", "picture name");
        pictureData.put("picture_description", "1"); 
        pictureData.put("content_type", "jpg");
        pictureData.put("original_filename", "base64:"+file[file.length-1]);
        pictureData.put("filename", file[file.length-1]);
        pictureData.put("picture_path", encodePicture(fileName));

        return pictureData;
    }

    public String encodePicture(String fileName) throws IOException{
        File picture = new File(fileName);
        return Base64.encodeToString(FileUtils.readFileToByteArray(picture), Base64.DEFAULT);
    }

}