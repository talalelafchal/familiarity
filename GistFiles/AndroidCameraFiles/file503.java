public class CardboardRenderer implements CardboardView.StereoRenderer {

    private static final String TAG = "CardboardRenderer";

    private static CardboardRenderer instance = null;
    private CardboardView surface;

    private static EGLContext eglContext = null;
    private Context mContext = null;

    // Indicates if SurfaceView.Renderer.onSurfaceCreated was called.
    // If true then for every newly created yuv image renderer createTexture()
    // should be called. The variable is accessed on multiple threads and
    // all accesses are synchronized on yuvImageRenderers' object lock.
    private boolean onSurfaceCreatedCalled;
    private int screenWidth;
    private int screenHeight;

    private static YuvImageRenderer yuvImageRenderer;
    private static OesImageRenderer oesImageRenderer;

    private static YuvDrawer yuvDrawer;
    private static OesDrawer oesDrawer;

    private static Thread renderFrameThread;
    private static Thread drawThread;

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 1000.0f;

    // Position the eye in front of the origin.
    final float eyeX = 0.0f;
    final float eyeY = 0.0f;
    final float eyeZ = 0.0f;

    // We are looking toward the distance
    final float lookX = 0.0f;
    final float lookY = 0.0f;
    final float lookZ = -100.0f;

    // Set our up vector. This is where our head would be pointing were we holding the camera.
    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;

    private float[] mCameraViewMatrix;
    private float[] mViewMatrix;
	 
    public CardboardRenderer(CardboardView surface, Context context) {
        this.surface = surface;
        surface.setRenderer(this);
        surface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
	      mContext = context;
        mCameraViewMatrix= new float[16];
        mViewMatrix = new float[16];
    }

    /**
     * Class used to display stream of YUV420 frames at particular location
     * on a screen. New video frames are sent to display using renderFrame()
     * call.
     */
    private static class YuvImageRenderer implements VideoRenderer.Callbacks {
        private CardboardView surface;
        private int[] yuvTextures = { 0, 0, 0 };
        // Pending frame to render. Serves as a queue with size 1. |pendingFrame| is accessed by two
        // threads - frames are received in renderFrame() and consumed in draw(). Frames are dropped in
        // renderFrame() if the previous frame has not been rendered yet.
        private VideoRenderer.I420Frame pendingFrame;
        private final Object pendingFrameLock = new Object();

        private RendererCommon.ScalingType scalingType;
        private boolean mirror;

        // Flag if renderFrame() was ever called
        private boolean seenFrame;

        // Cached layout transformation matrix, calculated from current layout parameters.
        private float[] layoutMatrix;
        // Texture sampling matrix.
        private float[] rotatedSamplingMatrix;
        // Layout properties update lock. Guards |updateLayoutProperties|, |screenWidth|,
        // |screenHeight|, |videoWidth|, |videoHeight|, |rotationDegree|, |scalingType|, and |mirror|.
        private final Object updateLayoutLock = new Object();

        // Viewport dimensions.
        private int screenWidth;
        private int screenHeight;

        // Video dimension.
        private int frameWidth;
        private int frameHeight;
        private int frameRotation;

        // Video dimension.
        private int videoWidth;
        private int videoHeight;

        private int rotationDegree;

        YuvImageRenderer(CardboardView surface, RendererCommon.ScalingType scalingType, boolean mirror) {
            Logging.d(TAG, "YuvImageRenderer.Create");
            this.surface = surface;
            this.scalingType = scalingType;
            this.mirror = mirror;
        }

        public synchronized void reset() {
            seenFrame = false;
        }

        private synchronized void release() {
            surface = null;
            synchronized (pendingFrameLock) {
                if (pendingFrame != null) {
                    VideoRenderer.renderFrameDone(pendingFrame);
                    pendingFrame = null;
                }
            }
        }

        private void createTextures() {
            Logging.d(TAG, "  YuvImageRenderer.createTextures " + " on GL thread:" +
                    Thread.currentThread().getId());
            // Generate 3 texture ids for Y/U/V and place them into |yuvTextures|.
            for (int i = 0; i < 3; i++)  {
                yuvTextures[i] = GlUtil.generateTexture(GLES20.GL_TEXTURE_2D);
            }
        }

        // Return current frame aspect ratio, taking rotation into account.
        private float frameAspectRatio() {
            if (frameWidth == 0 || frameHeight == 0) {
                return 0.0f;
            }
            return (frameRotation % 180 == 0) ? (float) frameWidth / frameHeight
                    : (float) frameHeight / frameWidth;
        }

        private void updateFrameDimensions(VideoRenderer.I420Frame frame) {
            synchronized (updateLayoutLock) {
                if (frameWidth != frame.width || frameHeight != frame.height || frameRotation != frame.rotationDegree) {

                }

                frameWidth = frame.width;
                frameHeight = frame.height;
                frameRotation = frame.rotationDegree;
            }
        }

        // drawing yuv frames
        private void draw(YuvDrawer drawer) {
            if (!seenFrame) {
                // No frame received yet - nothing to render.
                return;
            }

            final boolean isNewFrame;
            synchronized (pendingFrameLock) {
                isNewFrame = (pendingFrame != null);
            }

            if (isNewFrame) {
                rotatedSamplingMatrix = RendererCommon.rotateTextureMatrix(pendingFrame.samplingMatrix, pendingFrame.rotationDegree);
                layoutMatrix = RendererCommon.getLayoutMatrix(mirror, frameAspectRatio(), (float) screenWidth / (screenHeight / 2));

                if (pendingFrame.yuvFrame) {
                    drawer.uploadYuvData(yuvTextures, pendingFrame.width, pendingFrame.height, pendingFrame.yuvStrides, pendingFrame.yuvPlanes);
                }
                VideoRenderer.renderFrameDone(pendingFrame);
                pendingFrame = null;
            }

            final float[] texMatrix = RendererCommon.multiplyMatrices(rotatedSamplingMatrix, layoutMatrix);

            drawer.drawYuv(yuvTextures, texMatrix);
        }

        public void setScreenSize(final int screenWidth, final int screenHeight) {
            if (screenWidth == this.screenWidth && screenHeight == this.screenHeight) {
                return;
            }
            Logging.d(TAG,  "YuvImageRenderer.setScreenSize: " +
                    screenWidth + " x " + screenHeight);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        private void setSize(final int videoWidth, final int videoHeight, final int rotation) {
            if (videoWidth == this.videoWidth && videoHeight == this.videoHeight
                    && rotation == rotationDegree) {
                return;
            }
            synchronized (updateLayoutLock) {
                Logging.d(TAG, "YuvImageRenderer.setSize: " +
                        videoWidth + " x " + videoHeight + " rotation " + rotation);
                this.videoWidth = videoWidth;
                this.videoHeight = videoHeight;
                rotationDegree = rotation;
                Logging.d(TAG, "  YuvImageRenderer.setSize done.");
            }
        }


        @Override
        public void renderFrame(VideoRenderer.I420Frame frame) {
            if (surface == null) {
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            if (renderFrameThread == null) {
                renderFrameThread = Thread.currentThread();
            }

            synchronized (pendingFrameLock) {
                if (frame.yuvFrame) {
                    if (frame.yuvStrides[0] < frame.width || frame.yuvStrides[1] < frame.width / 2
                            || frame.yuvStrides[2] < frame.width / 2) {
                        Logging.e(TAG, "Incorrect strides " + frame.yuvStrides[0] + ", " +
                                frame.yuvStrides[1] + ", " + frame.yuvStrides[2]);
                        VideoRenderer.renderFrameDone(frame);
                        return;
                    }
                }

                if (pendingFrame != null) {
                    VideoRenderer.renderFrameDone(frame);
                    seenFrame = true;
                    return;
                }

                pendingFrame = frame;
                updateFrameDimensions(frame);
            }

            setSize(frame.width, frame.height, frame.rotationDegree);
            seenFrame = true;
            surface.requestRender();
        }
    }

    private static class OesImageRenderer implements VideoRenderer.Callbacks {

        CardboardView surface;
        private int oesTexture = -1;

        // Pending frame to render. Serves as a queue with size 1. |pendingFrame| is accessed by two
        // threads - frames are received in renderFrame() and consumed in draw(). Frames are dropped in
        // renderFrame() if the previous frame has not been rendered yet.
        private VideoRenderer.I420Frame pendingFrame;
        private final Object pendingFrameLock = new Object();
        private RendererCommon.ScalingType scalingType;
        private boolean mirror;

        // Flag if renderFrame() was ever called
        private boolean seenFrame;

        // Cached layout transformation matrix, calculated from current layout parameters.
        private float[] layoutMatrix;
        // Texture sampling matrix.
        private float[] rotatedSamplingMatrix;
        // Layout properties update lock. Guards |updateLayoutProperties|, |screenWidth|,
        // |screenHeight|, |videoWidth|, |videoHeight|, |rotationDegree|, |scalingType|, and |mirror|.
        private final Object updateLayoutLock = new Object();

        // Viewport dimensions.
        private int screenWidth;
        private int screenHeight;

        // Video dimension.
        private int frameWidth;
        private int frameHeight;
        private int frameRotation;

        // Video dimension.
        private int videoWidth;
        private int videoHeight;

        // This is the degree that the frame should be rotated clockwisely to have
        // it rendered up right.
        private int rotationDegree;

        private OesImageRenderer(CardboardView surface,
                                 RendererCommon.ScalingType scalingType, boolean mirror) {
            Logging.d(TAG, "OesImageRenderer.Create");
            this.surface = surface;
            this.scalingType = scalingType;
            this.mirror = mirror;
        }

        public synchronized  void reset() {
            seenFrame = false;
        }

        private synchronized  void release() {
            surface = null;
            synchronized (pendingFrameLock) {
                if (pendingFrame != null) {
                    VideoRenderer.renderFrameDone(pendingFrame);
                    pendingFrame = null;
                }
            }
        }

        private void createTextures() {
            Logging.d(TAG, "  OesImageRenderer.createTextures " + " on GL thread:" +
                    Thread.currentThread().getId());
            // OES Texture
            oesTexture = GlUtil.generateTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        }

        // Return current frame aspect ratio, taking rotation into account.
        private float frameAspectRatio() {
            if (frameWidth == 0 || frameHeight == 0) {
                return 0.0f;
            }
            return (frameRotation % 180 == 0) ? (float) frameWidth / frameHeight
                    : (float) frameHeight / frameWidth;
        }

        private void updateFrameDimensions(VideoRenderer.I420Frame frame) {
            synchronized (updateLayoutLock) {
                if (frameWidth != frame.width || frameHeight != frame.height || frameRotation != frame.rotationDegree) {

                }

                frameWidth = frame.width;
                frameHeight = frame.height;
                frameRotation = frame.rotationDegree;
            }
        }

        private void draw(OesDrawer drawer, boolean pano) {
            if (!seenFrame) {
                // No frame received yet - nothing to render.
                return;
            }

            final boolean isNewFrame;
            synchronized (pendingFrameLock) {
                isNewFrame = (pendingFrame != null);
            }

            if (isNewFrame) {
                rotatedSamplingMatrix = RendererCommon.rotateTextureMatrix(pendingFrame.samplingMatrix, pendingFrame.rotationDegree);
                rotatedSamplingMatrix = RendererCommon.multiplyMatrices(rotatedSamplingMatrix, RendererCommon.verticalFlipMatrix());    // Flip image to fit cylinder model texture coords
                layoutMatrix = RendererCommon.getLayoutMatrix(mirror, frameAspectRatio(), (float) screenWidth / (screenHeight / 2)); // for cardboardview scale property should be unit
                oesTexture = pendingFrame.textureId;
				VideoRenderer.renderFrameDone(pendingFrame);
                pendingFrame = null;
            }

            final float[] texMatrix = RendererCommon.multiplyMatrices(rotatedSamplingMatrix, layoutMatrix);
            drawer.drawOes(oesTexture, texMatrix, pano);
        }

        public void setScreenSize(final int screenWidth, final int screenHeight) {
            if (screenWidth == this.screenWidth && screenHeight == this.screenHeight) {
                return;
            }
            Logging.d(TAG, ". OesImageRenderer.setScreenSize: " +
                    screenWidth + " x " + screenHeight);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        private void setSize(final int videoWidth, final int videoHeight, final int rotation) {
            if (videoWidth == this.videoWidth && videoHeight == this.videoHeight
                    && rotation == rotationDegree) {
                return;
            }
            synchronized (updateLayoutLock) {
                Logging.d(TAG, ". OesImageRenderer.setSize: " +
                        videoWidth + " x " + videoHeight + " rotation " + rotation);
                this.videoWidth = videoWidth;
                this.videoHeight = videoHeight;
                rotationDegree = rotation;
                Logging.d(TAG, "  OesImageRenderer.setSize done.");
            }
        }

        @Override
        public void renderFrame(VideoRenderer.I420Frame frame) {
            if (surface == null) {
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            if (renderFrameThread == null) {
                renderFrameThread = Thread.currentThread();
            }

            synchronized (pendingFrameLock) {
                if (frame.yuvFrame) {
                    if (frame.yuvStrides[0] < frame.width || frame.yuvStrides[1] < frame.width / 2
                            || frame.yuvStrides[2] < frame.width / 2) {
                        Logging.e(TAG, "Incorrect strides " + frame.yuvStrides[0] + ", " +
                                frame.yuvStrides[1] + ", " + frame.yuvStrides[2]);
                        VideoRenderer.renderFrameDone(frame);
                        return;
                    }
                }

                if (pendingFrame != null) {
                    VideoRenderer.renderFrameDone(frame);
                    seenFrame = true;
                    return;
                }

                pendingFrame = frame;
                updateFrameDimensions(frame);
            }

            setSize(frame.width, frame.height, frame.rotationDegree);
            seenFrame = true;
            surface.requestRender();
        }

		public int getOesTexture() {
			return oesTexture;
		}
    }

    public static synchronized  void setView(CardboardView surface, Context context) {
        Logging.d(TAG, "CardboardRenderer.setView");
        instance = new CardboardRenderer(surface, context);
    }

    public static synchronized EGLContext getEGLContext() {
        return eglContext;
    }

    public static synchronized void YuvRemove(VideoRenderer.Callbacks renderer) {
        Logging.d(TAG, "CardboardRenderer.remove");
        if (instance == null) {
            throw new RuntimeException("Attempt to remove renderer before setting CardboardView");
        }

        synchronized (instance.yuvImageRenderer) {
            instance.yuvImageRenderer.release();
        }
    }

    /**
     * Creates VideoRenderer.Callbacks with top left corner at (x, y) and
     * resolution (width, height). All parameters are in percentage of
     * screen resolution.
     */
    public static synchronized YuvImageRenderer yuvCreate(RendererCommon.ScalingType scalingType, boolean mirror) {

        if (instance == null) {
            throw new RuntimeException( "Attempt to create yuv renderer before setting GLSurfaceView");
        }

        yuvImageRenderer = new YuvImageRenderer(instance.surface, scalingType, mirror);

        synchronized (instance.yuvImageRenderer) {
            if (instance.onSurfaceCreatedCalled) {
                // onSurfaceCreated has already been called for VideoRendererGui -
                // need to create texture for new image and add image to the
                // rendering list.
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                instance.surface.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        yuvImageRenderer.createTextures();
                        yuvImageRenderer.setScreenSize(instance.screenWidth, instance.screenHeight);
                        countDownLatch.countDown();
                    }
                });

                // Wait for task completion
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return yuvImageRenderer;
    }

    /**
     * Creates VideoRenderer.Callbacks with top left corner at (x, y) and
     * resolution (width, height). All parameters are in percentage of
     * screen resolution.
     */
    public static synchronized OesImageRenderer oesCreate(RendererCommon.ScalingType scalingType, boolean mirror) {

        if (instance == null) {
            throw new RuntimeException( "Attempt to create yuv renderer before setting GLSurfaceView");
        }

        oesImageRenderer = new OesImageRenderer(instance.surface, scalingType, mirror);

        synchronized (instance.oesImageRenderer) {
            if (instance.onSurfaceCreatedCalled) {
                // onSurfaceCreated has already been called for VideoRendererGui -
                // need to create texture for new image and add image to the
                // rendering list.
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                instance.surface.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        oesImageRenderer.createTextures();
                        oesImageRenderer.setScreenSize(instance.screenWidth, instance.screenHeight);
                        countDownLatch.countDown();
                    }
                });

                // Wait for task completion
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return oesImageRenderer;
    }

    public static synchronized void OesRemove(VideoRenderer.Callbacks renderer) {
        Logging.d(TAG, "CardboardRenderer.remove");
        if (instance == null) {
            throw new RuntimeException("Attempt to remove renderer before setting CardboardView");
        }

        synchronized (instance.oesImageRenderer) {
            instance.oesImageRenderer.release();
        }
    }

	/**
	 *  Release all the GLES resources and static variables
	 */
	public static synchronized void dispose() {
		if (instance == null) {
			return;
		}

		Logging.d(TAG, "CardboardRenderer dispose");
		synchronized (oesImageRenderer) {
			instance.oesImageRenderer.release();
			instance.oesImageRenderer = null;
			if (instance.oesDrawer != null) {
				instance.oesDrawer.release();
				instance.oesDrawer = null;

			}
		}

		synchronized (yuvImageRenderer) {
			instance.yuvImageRenderer.release();
			instance.yuvImageRenderer = null;
			if (instance.yuvDrawer != null) {
				instance.yuvDrawer.release();
				instance.yuvDrawer = null;
			}
		}
		renderFrameThread = null;
		drawThread = null;
		instance.surface = null;
		eglContext = null;
		instance = null;
	}
  
	@Override
    public void onNewFrame(HeadTransform headTransform) {
    }

	/**
	 * Requests to draw the contents from the point of view of an eye.
	 If distortion correction is enabled the GL context will be set to draw
	 into a framebuffer backed by a texture at the time of this call,
	 so if an implementor need to change the framebuffer for some rendering stage then the implementor
	 must reset the framebuffer to the one obtained via glGetIntegerv(GL_FRAMEBUFFER_BINDING, ...) afterwards.
	 */
    @Override
    public void onDrawEye(Eye eye) {
        if (drawThread == null) {
            drawThread = Thread.currentThread();
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set camera view to eye view
        Matrix.multiplyMM(mViewMatrix, 0, eye.getEyeView(), 0, mCameraViewMatrix, 0);

        // Set MVPs
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
		// Draw YUV frame
        synchronized (yuvImageRenderer) {
			Matrix.multiplyMM(yuvDrawer.getRectangle().getModelViewProjection(), 0,
					perspective, 0, yuvDrawer.getRectangle().getModelView().getArray(), 0);
        }

        // Draw OES frame
        synchronized (oesImageRenderer) {
			// Quad
			Matrix.multiplyMM(oesDrawer.getRectangle().getModelView().getArray(), 0,
					mViewMatrix, 0, oesDrawer.getRectangle().getModel().getArray(), 0);
			Matrix.multiplyMM(oesDrawer.getRectangle().getModelViewProjection(), 0,
					perspective, 0, oesDrawer.getRectangle().getModelView().getArray(), 0);
          
			oesImageRenderer.draw(oesDrawer, pano);
        }
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
		Logging.d(TAG, "CardboardRenderer.onSurfaceChanged");
        screenWidth = width;
        screenHeight = height;

        synchronized (yuvImageRenderer) {
            yuvImageRenderer.setScreenSize(screenWidth, screenHeight);
        }

        synchronized (oesImageRenderer) {
            oesImageRenderer.setScreenSize(screenWidth, screenHeight);
        }

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Logging.d(TAG, "CardboardRenderer.onSurfaceCreated");
        // Store render EGL context
        synchronized (CardboardRenderer.class) {
            eglContext = ((EGL10) EGLContext.getEGL()).eglGetCurrentContext();
            Logging.d(TAG, "CardboardRenderer EGL Context: " + eglContext);
        }

        synchronized (yuvImageRenderer) {
            yuvDrawer = new YuvDrawer();
			yuvImageRenderer.createTextures();
        }

		synchronized (oesImageRenderer) {
			oesDrawer = new OesDrawer();
			oesImageRenderer.createTextures();
		}

        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glClearColor(0.15f, 0.15f, 0.15f, 1.0f);
        Matrix.setLookAtM(mCameraViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

		onSurfaceCreatedCalled = true;
    }

    @Override
    public void onRendererShutdown() {
		Logging.d(TAG, "onRendererShutdown");
    }
}
