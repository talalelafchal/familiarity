import android.app.ProgressDialog;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Map;
import cz.msebera.android.httpclient.Header;

public class GetRequest {

    Context context;
    OnFinishListener listener;

    String url;
    Map<String, String> params;

    public GetRequest(
            Context context,
            String url,
            Map<String, String> params) {
        this.context = context;
        this.url = url;
        this.params = params;
    }
    
    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }
    
    public void execute() {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams request_params = new RequestParams();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            request_params.put(entry.getKey(), entry.getValue());
        }
        
        client.get(
                url,
                request_params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        if (listener != null) {
                            listener.onFailed(response);
                        }
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        if (progress_dialog != null) {
                            progress_dialog.dismiss();
                        }
                        if (listener != null) {
                            listener.onSuccessful(response);
                        }
                    }
                }
        );
    }

    public interface OnFinishListener{
        void onSuccessful(String result);
        void onFailed(String result);
    }

}
