package hw1.cardexc.com.less01_homework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Map<String, String> loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginData = new HashMap<>();
        loginData.put("test", "pass");

        final EditText textLogin = (EditText) findViewById(R.id.textLogin);
        final EditText textPass = (EditText) findViewById(R.id.textPass);
        final TextView textStatus = (TextView) findViewById(R.id.textStatus);
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredLogin = textLogin.getText().toString();

                if (loginData.get(enteredLogin) == null) {
                    textStatus.setText("wrong pass!");
                    return;
                }

                String enteredPass = textPass.getText().toString();

                if (!loginData.get(enteredLogin).equals(enteredPass)) {
                    textStatus.setText("wrong pass!");
                    return;
                }

                Intent logIntent = new Intent(getApplicationContext(), MainActivity.class);
                logIntent.putExtra("login", enteredLogin);
                startActivity(logIntent);

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
