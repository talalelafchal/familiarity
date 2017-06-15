  // usage 1
  button.setOnClickListener(new OnDebouncedClickListener() {
      @Override
      public void onDebouncedClick(View v) {
        // do some stuff when button clicked
      }
  });
  
  // usage 2
  button.setOnClickListener(new OnDebouncedClickListener(500) {
      @Override
      public void onDebouncedClick(View v) {
        // do some stuff when button clicked
      }
  });
  