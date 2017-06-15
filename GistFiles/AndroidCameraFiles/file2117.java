public class GifDisplayFrag extends Fragment {
    private static final String TAG = "GifDisplayFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = new GifWebView(getActivity(), GGMainActivity.gifFile, "480", "360");
        return v;
    }

}
