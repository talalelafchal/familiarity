GsonTransformer t = new GsonTransformer();
aq.transformer(t).ajax("http://hoge.com/api/v1/huga/user/detail.php?id=1", Profile.class, new AjaxCallback<Profile>(){
    public void callback(String url, Profile profile, AjaxStatus status) {
        Gson gson = new Gson();
        showResult("GSON Object:" + gson.toJson(profile), status);
    }
});