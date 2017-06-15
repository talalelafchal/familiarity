package com.example.menumaker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainFrag extends Fragment
{
	private EditText etNewMessage;
	private TextView tvHelloWorld;
	private Button btnSubmit;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.main_frag, container, false);
		
		setHasOptionsMenu(true);
		
		

		return view;
		
		
	}
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.main_frag, menu);
		
		etNewMessage = (EditText)getView().findViewById(R.id.etNewMessage);
		tvHelloWorld = (TextView)getView().findViewById(R.id.tvHelloWorld);
		btnSubmit = (Button)getView().findViewById(R.id.btnSubmit);
		
	} 
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.call :   startActivity(new Intent(Intent.ACTION_DIAL));
				return true;
			case R.id.camera : startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
				return true;
			case R.id.delete : tvHelloWorld.setText(""); etNewMessage.setText("");
				return true;
			case R.id.add :    etNewMessage.setVisibility(1); btnSubmit.setVisibility(1);
				return true;
			case R.id.info :   Toast.makeText(this.getActivity(), "Powered by Android", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.play :  startActivity(new Intent(android.content.Intent.CATEGORY_APP_MUSIC));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void onClick(View v)
	{
		tvHelloWorld.setText(etNewMessage.getText());
		
	}
	
}
