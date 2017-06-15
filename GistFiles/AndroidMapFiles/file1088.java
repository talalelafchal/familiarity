class MarkerDetails extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.marker_details, container, false);
    }
}

class SomeActivity extends FragmentActivity {
    public void newMarker(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MarkerDetails markerDetails = new MarkerDetails();
        fragmentTransaction.add(R.id.map_container, markerDetails); //MarkerDetails is not an instance of Fragment ?!?!
        fragmentTransaction.commit();
    }