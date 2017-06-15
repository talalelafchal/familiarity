    // Janky "fix" to prevent artefacts when embedding GoogleMaps in a sliding view.
    // https://github.com/jfeinstein10/SlidingMenu/issues/168
    
    // set background to transparent
    private void setMapTransparent(ViewGroup group) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup) {
                setMapTransparent((ViewGroup) child);
            } else if (child instanceof SurfaceView) {
                child.setBackgroundColor(0x00000000);
            }
        }
    }

    // call it on the SupportMapFragment somewhere
    setMapTransparent((ViewGroup) supportMapFragment.getView());
    