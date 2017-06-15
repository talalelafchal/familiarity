public class GifDisplayFrag extends Fragment {
    private static final String TAG = "GifDisplayFrag";
    public static final String GOOGLE_DRIVE = "com.google.android.apps.docs";
    public static final String GOOGLE_PLUS = "com.google.android.apps.plus";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = new GifWebView(getActivity(), GGMainActivity.gifFile, "480", "360");
        new DriveUpload().execute();
        return v;
    }

    private class DriveUpload extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "GlassPhotoDelay");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            try {
                // http://divingintoglass.blogspot.com/2014/01/how-to-upload-files-using-google-drive.html
                Uri imageUri = Uri.fromFile(new File(GGMainActivity.gifFile));
                Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setText("#glassgif #throughglass")
                        .setType("image/gif")
                        .setStream(imageUri )
                        .getIntent()
                        .setPackage(GOOGLE_DRIVE);

                startActivity(shareIntent);

                getActivity().finish();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
