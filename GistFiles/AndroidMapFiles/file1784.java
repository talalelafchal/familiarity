  // 3 click, 1000 ms
  // 连续 3 次点击，并且点击之间的间隔不能超过 1000ms，才能被回调。
  view.setOnClickListener(new OnMultipleClickListener(3, 1000) {
      @Override
      public void onMultipleClick(View v) {
        // do some stuff when multiple clicked.
      }
  });
