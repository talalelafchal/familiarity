.............
  .........
  
  // this TestFragment fragment content info windows UI
      private TestFragment testFragment;

@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.maps_fragment, container, false);

      
        mMapView = (customeMapview) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.setCustomEventListener(new OnCustomEventListener() {
            @Override
            public void onEvent(MotionEvent event) {
                if (testFragment != null) {
                    // I only care if the event is an DOWN action
                    


                    if (testFragment.isVisible()) {
                        // create a rect for storing the fragment window rect
                        Rect r = new Rect(0, 0, 0, 0);
                        // retrieve the fragment's windows rect
                        testFragment.getView().getHitRect(r);
                        // check if the event position is inside the window rect
                        boolean intersects = r.contains((int) event.getX(), (int) event.getY());
                        // if the event is not inside then we can close the fragment
                      //if intersects= true it means u click out of the fragment(infowindow)
                        if (intersects) {

                            FragmentTransaction fragmentTransaction;
                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(testFragment).commit();
                            // notify that we consumed this event

                        }
                    }


                }
            }
        });

    }




............
  ...........
  
  //lets setup our Custom + static pos info window screen
  @Override
    public boolean onMarkerClick(Marker marker) {

        testFragment = new TestFragment(getActivity(), marker);
        android.support.v4.app.FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.fram_info_content, testFragment);
        ft1.commit();


        return true;
    }



........
  ............
  
  
  //custome listner to fetch data from Custom map view because map does't allow  anu touch/click listner 
    public interface OnCustomEventListener {
        void onEvent(MotionEvent ev);
    }