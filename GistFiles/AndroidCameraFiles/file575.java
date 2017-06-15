private static final double MAX_ASPECT_DISTORTION = 0.15;
private static final float ASPECT_RATIO_TOLERANCE = 0.01f;

//desiredWidth and desiredHeight can be the screen size of mobile device
private static SizePair generateValidPreviewSize(Camera camera, int desiredWidth,
      int desiredHeight) {
    Camera.Parameters parameters = camera.getParameters();
    double screenAspectRatio = desiredWidth / (double) desiredHeight;
    List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
    List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
    SizePair bestPair = null;
    double currentMinDistortion = MAX_ASPECT_DISTORTION;
    for (Camera.Size previewSize : supportedPreviewSizes) {
      float previewAspectRatio = (float) previewSize.width / (float) previewSize.height;
      for (Camera.Size pictureSize : supportedPictureSizes) {
        float pictureAspectRatio = (float) pictureSize.width / (float) pictureSize.height;
        if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
          SizePair sizePair = new SizePair(previewSize, pictureSize);

          boolean isCandidatePortrait = previewSize.width < previewSize.height;
          int maybeFlippedWidth = isCandidatePortrait ? previewSize.width : previewSize.height;
          int maybeFlippedHeight = isCandidatePortrait ? previewSize.height : previewSize.width;
          double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;
          double distortion = Math.abs(aspectRatio - screenAspectRatio);
          if (distortion < currentMinDistortion) {
            currentMinDistortion = distortion;
            bestPair = sizePair;
          }
          break;
        }
      }
    }

    return bestPair;
  }


 private static class SizePair {
    private Size mPreview;
    private Size mPicture;

    public SizePair(Camera.Size previewSize, Camera.Size pictureSize) {
      mPreview = new Size(previewSize.width, previewSize.height);
      if (pictureSize != null) {
        mPicture = new Size(pictureSize.width, pictureSize.height);
      }
    }

    public Size previewSize() {
      return mPreview;
    }

    @SuppressWarnings("unused") public Size pictureSize() {
      return mPicture;
    }
  }