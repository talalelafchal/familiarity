public class customeMapview extends MapView {
    MapsFragment.OnCustomEventListener mListener;
    private boolean ismoved = false;


    public customeMapview(Context context) {
        super(context);
    }

    public customeMapview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public customeMapview(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public customeMapview(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

/**
*onIterceptTouchListner Handle two things
*1) when map move it stop listner
*2) when touch on map but not swipe map  like tap on marker tap on plan area at that time this call
**/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
      
      //when map move stop to give data to listner
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            ismoved = true;
        }

        if (ev.getAction() == MotionEvent.ACTION_UP) {

          // if map not moved just tap  give data to listner
            if (!ismoved) {

                if (mListener != null)
                    mListener.onEvent(ev);
            }
            ismoved = false;

        }
        return super.onInterceptTouchEvent(ev);
    }


    public void setCustomEventListener(MapsFragment.OnCustomEventListener eventListener) {
        mListener = eventListener;
    }
}