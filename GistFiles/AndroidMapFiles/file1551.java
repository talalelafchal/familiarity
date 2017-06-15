aQuery.id(R.id.activity_main_image).image("http://hoge.com/images/huga.jpg", true, true, 200, 0, new AbstractBitmapAjaxCallback() {
    @Override
    public Bitmap transform(String url, byte[] data, AjaxStatus status) {
        final File file = status.getFile();
        final String path = file != null ? file.getAbsolutePath() : null;
        return getResizedImage(path, data, 200, false, 0);
    }
});