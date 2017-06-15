public Observable<SearchResult> search(@NotNull EditText searchView) {
  return RxTextView.textChanges(searchView) // In production, share this text view observable, don't create a new one each time
    .map(CharSequence::toString)
    .debounce(500, TimeUnit.MILLISECONDS)   // Avoid getting spammed with key stroke changes
    .filter(s -> s.length() > 1)            // Only interested in queries of length greater than 1
    .observeOn(workerScheduler)             // Next set of operations will be network so switch to an IO Scheduler (or worker)
    .switchMap(query -> searchService.query(query))   // Take the latest observable from upstream and unsubscribe from any previous subscriptions
    .onErrorResumeNext(Observable.empty()); // <-- This will terminate upstream (ie. we will stop receiving text view changes after an error!)
}
public Observable<SearchResult> search(@NotNull EditText searchView) {
  return RxTextView.textChanges(searchView) // In production, share this text view observable, don't create a new one each time
    .map(CharSequence::toString)
    .debounce(500, TimeUnit.MILLISECONDS)   // Avoid getting spammed with key stroke changes
    .filter(s -> s.length() > 1)            // Only interested in queries of length greater than 1
    .observeOn(workerScheduler)             // Next set of operations will be network so switch to an IO Scheduler (or worker)
    .switchMap(query -> searchService.query(query) // Take the latest observable from upstream and unsubscribe from any previous subscriptions
               .onErrorResumeNext(Observable.empty()); // <-- This fixes the problem since the error is not seen by the upstream observable
}