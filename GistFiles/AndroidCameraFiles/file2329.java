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
        View rootView = inflater.inflate(R.layout.builder, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new BackgroundGif().execute();
    }

    private class BackgroundGif extends AsyncTask<Void, Bitmap, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG, "GlassPhotoDelay");
            String gifFile = SimpleFileUtils.getDir() + File.separator + System.currentTimeMillis() + ".gif";

            // http://stackoverflow.com/a/20277649/974800
            ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
            for (String filename : MainActivity.listOfFiles) {
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

            for (String filename : MainActivity.listOfFiles) {
                File f = new File(filename);
                if (f.exists())
                    f.delete();
            }

            return gifFile;
        }

        @Override
        protected void onPostExecute(String params) {
            flowControl.startDisplay();
        }
    }


}