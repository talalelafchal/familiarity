/**
 * Most of this is boilerplate, just an example to show how you tie an ImageView to the
 * ImageDownloader
 */
public class ImageDownloaderActivity extends Activity implements LoaderCallbacks<Cursor> {

    static final String PHOTO_CACHE_DIR = "photo_cache"
    static final String KEY_LAST_IMG_CACHE_CLEANUP = "last_image_cache_cleanup";
    static final long IMG_CACHE_AGE = 5*24*60*60*1000; // 5 days without being touched
    
    ImageDownloader downloader;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final File cacheDir = new File(ctx.getCacheDir(), PHOTO_CACHE_DIR)
        this.downloader = new ImageDownloader( cacheDir );
        
        /*
         * Note: a background service (perferably) or a main activity should periodically run 
         * a cache cleanup task like so:
         
	    final long now = System.currentTimeMillis();
        long lastImgCleanup = prefs.getLong( 
                KEY_LAST_IMG_CACHE_CLEANUP, 
	            now - IMG_CACHE_AGE - 1 );
	    
	    if ( lastImgCleanup + IMG_CACHE_AGE < now ) {
	    
            // run image cleanup
            ImageDownloader.ClearCacheFilesTask task = 
                    new ImageDownloader.ClearCacheFilesTask(
                        cacheDir, 
                        IMG_CACHE_AGE ) {
                   
                    protected void onPostExecute(Integer result) {
                        Log.d(TAG,"Deleted " + result + " photos older than " + 
                                new Date(now-IMG_CACHE_AGE) );
                        prefs.edit()
                            .putLong( KEY_LAST_IMG_CACHE_CLEANUP, now )
                            .commit();
                    }
                };
                
            task.execute();         
         }
         
         */
    }
    
    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data ) {
        this.currentCursor = data;

        // if this is the first launch, create the view
        if ( findViewById( R.id.label_title ) == null ) {
            setContentView( R.layout.post_detail );
        }
        
        getNextCursorResult();
    }

    /**
     * Move to the next result & load data into the view
     */
    protected void getNextCursorResult() {
        Cursor data = this.currentCursor;
        if ( data == null ) {
            Log.e(TAG,"No current cursor!!!!!!!");
            return;
        }

        if ( ! data.moveToNext() ) {
            Log.w(TAG,"No data from cursor!");
            finish();
            return;
        }
        
        String avatarURL = data.getString( data.getColumnIndex( Post.Cols.USER_AVATAR_URL ) );
        Log.d(TAG,"Downloading avatar: " + avatarURL);
        ImageView avatarView = (ImageView)findViewById( R.id.img_avatar );
        
        /****** THIS IS WHERE THE MAGIC HAPPENS ******/
        downloader.download( avatarURL, avatarView );
        /*************** END MAGIC *******************/
    }
}