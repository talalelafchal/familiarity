/*
 *	カスタムInfoAdapter
 * 	バルーンには以下の 3 要素を表示させる
 * 
 * 	・　Title
 * 	・　Snipet
 * 	・　画像（インターネット上から取得）
 * 
 *	マーカータップ時にバルーンを表示されたっきりだと
 *	非同期で取得してきた画像が反映されないので、
 *	非同期処理が完了した時点でバルーンを再描画してやる必要がある
 */
public class CustomInfoAdapter implements InfoWindowAdapter {


        private final View mWindow;
        String lastMarkerId;


        public CustomInfoAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }
    
        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }
        
        @Override
        public View getInfoContents(Marker marker) {
        	//　onPostExecute から呼ばれた場合は再描画する
        	boolean isRedraw = (marker != null && marker.isInfoWindowShown());
        	if(isRedraw) marker.showInfoWindow();

            return null;
        }
/**
 *     
 */
        private void render(final Marker marker, View view) {
        	
        	if (marker.getId().equals(lastMarkerId)){ return;                       }
        	else                                    { lastMarkerId = marker.getId();}


            final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.balloon_progress);
            final ImageView   imageView   = (ImageView)view.findViewById(R.id.infoWindow_image);
            AsyncTask<String, Void, WeakReference<Bitmap>> asyncTask = new AsyncTask<String, Void, WeakReference<Bitmap>>(){
				@Override
				protected void onPreExecute() {
					progressBar.setVisibility(View.VISIBLE);
		            imageView.setVisibility(View.GONE);
				}
				@Override
				protected WeakReference<Bitmap> doInBackground(String... params) {
					return Hoge.fuga();	// Hoge.fuga() はインターネット上から画像を取得するメソッド
				}
				@Override
				protected void onPostExecute(WeakReference<Bitmap> result) {
					progressBar.setVisibility(View.GONE);
					imageView.setImageBitmap(result.get());
					imageView.setVisibility(View.VISIBLE);
					getInfoContents(marker); // 画像取得後に改めて表示させるのがミソ
				}
            };
            asyncTask.execute(IMAGE_URL); // URL は上手いこと持ってきてくだち


            TextView title = (TextView) view.findViewById(R.id.infoWindow_title);
            title.setText(marker.getTitle());


            TextView snippet = (TextView) view.findViewById(R.id.infoWindow_snippet);
            snippet.setText(marker.getSnippet());
        }    
    }
