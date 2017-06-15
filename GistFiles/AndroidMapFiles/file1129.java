//...

public void onCreate(Bundle savedInstanceState) 
{
	super.onCreate(savedInstanceState);
	File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
	ListDir(root);
	isPlaying = false;
	c = getApplicationContext();
	index = 0;
}

void ListDir(File f)
{
	File[] files = f.listFiles();
	fileList.clear();
	for (File file : files){
		String fullPath = file.getAbsolutePath();
		String fileNameArr[] = fullPath.split("\\.");
		String extension = fileNameArr[fileNameArr.length-1];
					
		if(extension.equalsIgnoreCase("mp3"))
		{					
			Uri uri = Uri.parse(file.getPath());
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		        mmr.setDataSource(c, uri);
		                        
		        //get title
		        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		        
		        MP3File m = new MP3File(file.getPath(), title);
		        mp3FileMap.put((Integer)index, m);
		        fileList.add(title);
		        mmr.release();
		        index++;
		}
	}

	ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, 
			android.R.layout.simple_list_item_1, 
			fileList);
	setListAdapter(directoryList);
}
//...