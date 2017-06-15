import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;
import rx.Observable;

public class RxJavaFunActivity extends Activity {

    public class Usr {

        private Integer id;
        private String name;
        private String username;
        private String email;
        private String phone;
        private String website;
    }

    public interface UsrService {

        @GET("/users") Observable<List<Usr>> get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String API_URL = "http://jsonplaceholder.typicode.com";
        new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(UsrService.class)
                .get()
                .flatMap(users -> Observable.from(users))
                .take(5)
                .subscribe(usr -> Log.d("nevin", usr.id + "-------------" + usr.email));
    }
}
