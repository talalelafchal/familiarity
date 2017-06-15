package linz.jku;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.ID3v1;
import service.ShakeContactService;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * Get the list of songs in the device
 *
 */
public class GetMp3List extends AsyncTask<Context, Void, List<Mp3Data>> {

	// List of Songs
	private List<Mp3Data> songList;
	Cursor musiccursor;
	int music_column_index;
	int count;
	MediaPlayer mMediaPlayer;
	
	@Override
	protected List<Mp3Data> doInBackground(Context... arg0) {
		// Get the context
		Context c = arg0[0];
		
		songList = new ArrayList<Mp3Data>();
		System.gc();
		// Get the song played, including data, name, id and size
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE };
		musiccursor = c.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null,
				null);
		count = musiccursor.getCount();

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			

			// Get the index of the columns
			int dataColumn = musiccursor
					.getColumnIndex(MediaStore.Audio.Media.DATA);

			// Move the cursor and get the list of songs in the device
			// as MP3File
			musiccursor.moveToFirst();
			while (!musiccursor.isAfterLast()) {

				String filename = musiccursor.getString(dataColumn);

				MP3File mp3file;
				try {
					mp3file = new MP3File(new File(filename));
					if (mp3file.hasID3v1Tag()) {
						ID3v1 tag = mp3file.getID3v1Tag();
						Mp3Data m = new Mp3Data(tag.getArtist(),
								tag.getSongTitle(), tag.getAlbum(), Uri.parse(filename));
						songList.add(m);
					}
					mp3file = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				musiccursor.moveToNext();
			}
		}
		return songList;
	}
	
	@Override
	protected void onPostExecute(List<Mp3Data> list) {		
		ShakeContactService.setMp3List(list);
	}

}
