class getPhotoData extends UrlCacheManager {
        public getPhotoData(Context contextx) {
                super(contextx);
                setReadTimeOut(60000);
                setConnTimeOut(60000);
        }

        @Override
        public void postresult(Object result2) {
                // do something with result
        }
}
getPhotoData getphotodata = new getPhotoData(this);
getphotodata.getData(url);