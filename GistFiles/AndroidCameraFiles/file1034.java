public class GifBuilderFrag extends Fragment {
    private static final String TAG = "GifBuilderFrag";
    private GifFlowControl flowControl;
    private ImageView curImg;
    private ProgressBar progressBar;
    private AsyncTask updater;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        flowControl = (GifFlowControl) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.builder2, container, false);

        curImg = (ImageView) rootView.findViewById(R.id.cur_img);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new BackgroundGif().execute();
        updater = new ProgressUpdater().execute();
    }

    @Override
    public void onPause() {
        updater.cancel(true);
        super.onPause();
    }


    private class ProgressUpdater extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int count = 0;
            try {
                while (true) {
                    publishProgress((count++ % 100));
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

    }

    private class BackgroundGif extends AsyncTask<Void, Bitmap, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG, "GlassPhotoDelay");
            String gifFile = SimpleFileUtils.getDir() + File.separator + System.currentTimeMillis() + ".gif";

            ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
            for (String filename : GGMainActivity.listOfFiles) {
                File f = new File(filename);
                if (!f.exists())
                    continue;
                Bitmap b = ImageTools.generatePic(filename);
                bitmaps.add(b);
                publishProgress(b);
            }
            byte[] bytes = ImageTools.generateGIF(bitmaps);
            SimpleFileUtils.write(gifFile, bytes);
            String newGifFile = SimpleFileUtils.getSdDir() + File.separator
                    + "DCIM" + File.separator + "Camera" + File.separator
                    + System.currentTimeMillis() + ".gif";
            try {
                SimpleFileUtils.copy(gifFile, newGifFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String filename : GGMainActivity.listOfFiles) {
                File f = new File(filename);
                if (f.exists())
                    f.delete();
            }

            return gifFile;
        }

        protected void onProgressUpdate(Bitmap... progress) {
            curImg.setImageBitmap(progress[0]);
        }

        @Override
        protected void onPostExecute(String params) {
            Log.d(TAG, "creating card with params: " + params);
            flowControl.startDisplay();
        }
    }


}