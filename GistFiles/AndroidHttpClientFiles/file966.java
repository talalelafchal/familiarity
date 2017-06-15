package com.example.androidcallback;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidcallback.HttpGetAsyncTask.OnHttpGetListener;

public class MainActivity extends Activity implements OnHttpGetListener,
		OnClickListener {

	TextView textViewPokemonName;
	EditText editTextPokemonId;
	Button buttonSearchPokemonId;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textViewPokemonName = (TextView) findViewById(R.id.textViewPokemonName);
		editTextPokemonId = (EditText) findViewById(R.id.editTextPokemonId);
		buttonSearchPokemonId = (Button) findViewById(R.id.buttonSearchPokemonId);

		buttonSearchPokemonId.setOnClickListener(this);
		searchPokemonById(99);
	}

	private void searchPokemonById(int pokemonId) {
		HttpGetAsyncTask task = new HttpGetAsyncTask(
				"http://pokeapi.co/api/v1/pokemon/" + pokemonId);
		task.setOnHttpGetListener(this);
		task.execute();
	}

	@Override
	public void onHttpGetSuccess(String result) {
		try {
			JSONObject json = new JSONObject(result);
			String pokemonName = json.getString("name");

			textViewPokemonName.setText(pokemonName);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		progressDialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		String id = editTextPokemonId.getText().toString();
		searchPokemonById(Integer.parseInt(id));
	}

	@Override
	public void onHttpGetPreExecute() {
		progressDialog = ProgressDialog.show(this, "Pokemon Search",
				"Searching Pokemon please wait", true);
	}

}