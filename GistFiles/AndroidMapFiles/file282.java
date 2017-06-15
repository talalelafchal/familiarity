MyPojo pojo = new MyPojo();

Request request = new Request(){
    url = "http://host.com/api";
    method = HTTP.POST;
    data = pojo;
    header = new HashMap(){
        put("X-AUTH-TOKEN","ASDFN1309ASDF93JJAS471JJ3SNE");
    }
    public void onSuccess(Response res){
        //do something on success
    }
    public void onError(Response res){
        //do something on error
    }
}

request.make();


/*
Posible configurations
*/

Request.getConfiguration().disableLoggin();
Request.getConfiguration().enableLoggin();

//The Data supported are any Pojo or a Map

