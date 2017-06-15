package com.tumblr.taabouzeid.file.explorer;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class StartingPoint extends Activity {
	TextView tvLocation;
	ListView lvFiles;
	String currentDirectory;
	ImageButton btnGoUp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initializeComponents();
		listFiles(currentDirectory);
	}

	private void listFiles(String directory) {
		File file = new File(directory);
		if (file.canRead() && file.isDirectory()) {
			currentDirectory = file.getAbsolutePath();
			tvLocation.setText(currentDirectory);
			File[] files = file.listFiles();
			ArrayList<String> filenames = new ArrayList<String>();
			for(File f : files){
				filenames.add(f.getName());
			}
			lvFiles.setAdapter(new ArrayAdapter<Object>(
					getApplicationContext(), R.layout.simple_list_item,
					filenames.toArray()));
		} else if(file.isFile()) {
			openFile(file);
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
	}

	private void openFile(File file) {
		Uri uri = Uri.fromFile(file);
		String extension = FilenameUtils.getExtension(file.getName());
		MimeTypeMap mtm = MimeTypeMap.getSingleton();
		String mimeType = mtm.getMimeTypeFromExtension(extension);
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(uri, mimeType);

		try {
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Unknown File Type",
					Toast.LENGTH_LONG).show();
		}
	}

	private void initializeComponents() {
		btnGoUp = (ImageButton) findViewById(R.id.btnGoUp);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		lvFiles = (ListView) findViewById(R.id.lvFiles);
		currentDirectory = Environment.getExternalStorageDirectory().toString();
		registerForContextMenu(lvFiles);
		lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listFiles(currentDirectory + File.separator
						+ ((TextView) view).getText().toString());
			}
		});
	}

	public void onUp(View view) {
		File file = new File(currentDirectory).getParentFile();
		if (file != null) {
			listFiles(file.getAbsolutePath());
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}
}