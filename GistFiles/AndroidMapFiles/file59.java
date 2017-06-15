aQuery.ajax("http://hoge.com/api/v1/huga/animal/list.php?page=1&limit=20", String.class, new AjaxCallback<String>() {
    @Override
    public void callback(String url, String result, AjaxStatus status) {
        // APIのレスポンスが文字列で返ってくる
    }
});