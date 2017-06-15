//Oncreate
...
    if(savedInstanceState!=null){
    	restoreState(savedInstanceState);
    }
...

  /** Save the last viewed position of the list */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(Constant.OVERVIEW_ITEM_LIST_SAVE,
				mListOverview.getFirstVisiblePosition());		
	}

	private void restoreState(Bundle savedInstanceState) {
		int selectedItem = savedInstanceState
				.getInt(Constant.OVERVIEW_ITEM_LIST_SAVE);
		mListOverview.smoothScrollToPosition(selectedItem);
		mListOverview.setSelection(selectedItem);
	}