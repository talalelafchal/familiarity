// We often see sequential method call.
AlertDialog.Builder builer = new AlertDialog.Builder(this);
builer.setTitle("タイトル");
builer.setMessage("メッセージ");
builer.setPositiveButton(android.R.string.ok, this.listener);

// We can use method chain.
AlertDialog.Builder builer = new AlertDialog.Builder(this);
    .setTitle("タイトル")
    .setMessage("メッセージ")
    .setPositiveButton(android.R.string.ok, this.listener);

// We often use method chain in RxJava/Promise
Observable<Moke> o = this.sendRequest(...)
    .map(new Function<>{...})
    .map(new Function<>{...});
    
// In this case, if we use sequential method call...
Observable<Moke> o = this.sendRequest(...);
o = o.map(new Function<>{...}); // o becomes another object
o = o.map(new Function<>{...}); // o becomes another object

