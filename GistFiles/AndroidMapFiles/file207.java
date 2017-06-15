if (null != camera) {
    try {
        camera.stopPreview();
        camera.setPreviewCallback(null);
        camera.release();
        camera = null;
    } catch(RuntimeException e){
        //handle exception
    }
}