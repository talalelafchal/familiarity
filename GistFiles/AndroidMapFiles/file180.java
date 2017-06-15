package async.waleed.rx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Integer[] data = {1, 2, 3, 4};

        Observable.fromArray(data)
                .map(value -> value * value)
                .map(value -> Integer.toString(value))
                .forEach(string -> Log.i("Android", string));
    }
    
}
