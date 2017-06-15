public void touchEventHandler(@NotNull View view) {
  final ConnectedObservable<MotionEvent> motionEventObservable = RxView.touches(view).publish();
  // Capture down events
  final Observable<MotionEvent> downEventsObservable = motionEventObservable
    .filter(event -> event.getAction() == MotionEvent.ACTION_DOWN);
  // Capture up events
  final Observable<MotionEvent> upEventsObservable = motionEventObservable
    .filter(event -> event.getAction() == MotionEvent.ACTION_UP);

  // Show a red circle at the position where the down event ocurred
  subscriptions.add(downEventsObservable.subscribe(event ->
      view.showCircle(event.getX(), event.getY(), Color.RED)));
  // Show a blue circle at the position where the up event ocurred
  subscriptions.add(upEventsObservable.subscribe(event ->
      view.showCircle(event.getX(), event.getY(), Color.BLUE)));
  // Connect the source observable to begin emitting events
  subscriptions.add(motionEventObservable.connect());
}