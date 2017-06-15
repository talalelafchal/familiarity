// getList and  getDetail are retofit methods which return observers

return getList().retryWhen(new RetryWithDelay(time, count)).flatMap(new Func1 < Response, Observable < Object >> () { 
 @Override  public Observable < Object > call(Response response) {
  // get list and release each item from list
  return Observable.from(response.getlist());
 }
}).flatMap(new Func1 < Object, Observable < Response >> () { 
 @Override  public Observable < Response > call(Object object) {
  // calling the detail api from each item of list
  return getDetail(object.getId()).retryWhen(new RetryWithDelay(time, count)); 
 }
}).map(new Func1 < Response, ResultObject > () { 
 @Override  public ResultObject call(Response response) {  
  // perfom operation
  return null;
 }
}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());