SurfaceView view = new SurfaceView(this);
c.setPreviewDisplay(view.getHolder());
c.startPreview();
c.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback)