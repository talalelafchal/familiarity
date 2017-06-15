Map<String, Object> params = new HashMap<String, Object>();
params.put("name", "dog");
aQuery.ajax("http://hoge.com/api/v1/huga/animal/add.php", params, String.class, new AjaxCallback<String>() {
    @Override
    public void callback(String url, String result, AjaxStatus status) {
        // APIのレスポンスが文字列で返ってくる
    }
});