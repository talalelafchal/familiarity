package com.example.menumaker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity
{
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		FragmentManager fm = getSupportFragmentManager();
		MainFrag mf = new MainFrag();
		
		fm.beginTransaction()
		  .add(R.id.root_layout_default, mf)
		  .commit();
		
		fm.executePendingTransactions();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		return super.onOptionsItemSelected(item);
	}

	
}
