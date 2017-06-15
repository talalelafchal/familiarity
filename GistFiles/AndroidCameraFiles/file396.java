    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mOperation1Btn = (ToggleButton) view.findViewById(R.id.operation1);
        mOperation1Btn.setOnClickListener(new OnDebouncedClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                onToolbarOperation1Clicked(mOperation1Btn.isChecked());
            }
        });
    }
    
    protected abstract void onToolbarOperation1Clicked(boolean isChecked);