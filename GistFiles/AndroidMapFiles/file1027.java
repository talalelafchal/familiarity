public int pointToPosition(int x, int y) {    	

    	ArrayList<CarouselItem> fitting = new ArrayList<CarouselItem>();
    	
    	for(int i = 0; i < mAdapter.getCount(); i++){

    		CarouselItem item = (CarouselItem)getChildAt(i);

    		Matrix mm = item.getCIMatrix();
    		float[] pts = new float[3];
    		
    		pts[0] = item.getLeft();
    		pts[1] = item.getTop();
    		pts[2] = 0;
    		
    		mm.mapPoints(pts);
    		
    		int mappedLeft = (int)pts[0];
    		int mappedTop =  (int)pts[1];
    		    		
    		pts[0] = item.getRight();
    		pts[1] = item.getBottom();
    		pts[2] = 0;
    		
    		mm.mapPoints(pts);

    		int mappedRight = (int)pts[0];
    		int mappedBottom = (int)pts[1];
    		
    		if(mappedLeft < x && mappedRight > x & mappedTop < y && mappedBottom > y)
    			fitting.add(item);
    		
    	}

    	//Collections.sort(fitting);
    	if(fitting.size() != 0)
      {
        // index of the closest to selected fitting item
        // initialized with first item index
        int correctItemIndex = fitting.get(0).getIndex();
        // steps we need to get from closest item to selected
        // initialized with some big value
        int correctItemSteps = mAdapter.getCount();

        for(int i = 0; i < fitting.size(); i++) {
          // getting current item index
          int pos = fitting.get(i).getIndex();
          int steps; // getting steps from current item to selected

          // checking clockwise or anticlockwise is closer
          if(pos - mSelectedPosition > mAdapter.getCount() - pos)
            steps = mAdapter.getCount()-pos;
          else
            steps = pos - mSelectedPosition;

          // if current element is closer to selected
          // than the one that is marked as closest
          // then marking current element as closest
          if(steps < correctItemSteps) {
            correctItemSteps = steps;
            correctItemIndex = pos;
          }
        }
    		return correctItemIndex;
      }
    	else
    		return -1; // returns -1 of none of items was touched
    }