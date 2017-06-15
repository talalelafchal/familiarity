import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

/**
 * This file is based on the following AOSP sourcecode:
 * cts/tests/tests/opengl/src/android/opengl/cts/FramebufferTest.java
 */
public class WindowSurface {
    private static final String TAG = WindowSurface.class.getSimpleName();

    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    private EGLConfig mEGLConfig = null;
    private int mGlEsVersion = -1;

    /**
     * Associates an EGL surface with the native window surface.
     *
     * @param surface May be a Surface or SurfaceTexture.
     */
    public WindowSurface(Surface surface) {
        this(null, surface, 2);
    }

    /**
     * Associates an EGL surface with the native window surface.
     *
     * @param sharedContext The context to share, or null if sharing is not desired.
     * @param surface May be a Surface or SurfaceTexture.
     * @param glEsVersion The EGL_CONTEXT_CLIENT_VERSION to choose, must be 2 or 3.
     */
    public WindowSurface(EGLContext sharedContext, Object surface, int glEsVersion) {
        // State check.
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }

        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("surface already created");
        }

        // Arguments check.
        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT; // context sharing is not desired.
        }

        if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture)) {
            throw new RuntimeException("invalid surface: " + surface);
        }

        if (glEsVersion != 2 && glEsVersion != 3) {
            throw new RuntimeException("invalid glEsVersion: " + glEsVersion);
        }

        // Prepares EGL display.
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }

        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL14");
        }

        // Try to get a GLES3 context, if requested.
        if (glEsVersion == 3) {
            Log.logDebug(TAG, "Trying GLES 3");
            EGLConfig config = getConfig(3);
            if (config != null) {
                int[] attributes = {
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                        EGL14.EGL_NONE
                };
                EGLContext context = EGL14.eglCreateContext(mEGLDisplay,
                        config,
                        sharedContext,
                        attributes,
                        0);

                if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                    Log.logDebug(TAG, "Got GLES 3 config.");
                    mEGLConfig = config;
                    mEGLContext = context;
                    mGlEsVersion = 3;
                }
            }
        }

        // Fall back to GLES 2 if GLES 3 attempt failed.
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            EGLConfig config = getConfig(2);
            if (config == null) {
                throw new RuntimeException("Unable to find a suitable EGLConfig");
            }
            int[] attributes = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            EGLContext context = EGL14.eglCreateContext(
                    mEGLDisplay,
                    config,
                    sharedContext,
                    attributes,
                    0);
            checkEglError("eglCreateContext");
            mEGLConfig = config;
            mEGLContext = context;
            mGlEsVersion = 2;
        }

        // Confirm with query.
        int[] values = new int[1];
        EGL14.eglQueryContext(
                mEGLDisplay,
                mEGLContext,
                EGL14.EGL_CONTEXT_CLIENT_VERSION,
                values,
                0);
        Log.logDebug(TAG, "EGLContext created, client version " + values[0]);

        // Create a window surface, and attach it to the Surface we received.
        int[] surfaceAttributes = {
                EGL14.EGL_NONE
        };
        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(
                mEGLDisplay,
                mEGLConfig,
                surface,
                surfaceAttributes,
                0);
        checkEglError("eglCreateWindowSurface");
        if (eglSurface == null) {
            throw new RuntimeException("surface was null");
        }

        mEGLSurface = eglSurface;
    }

    /**
     * Returns the GLES version this context is configured for (currently 2 or 3).
     */
    public int getGlEsVersion() {
        return mGlEsVersion;
    }

    /**
     * Makes our EGL context and surface current.
     */
    public void makeCurrent() {
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            // called makeCurrent() before create?
            Log.e(TAG, "makeCurrent w/o display");
        }

         // Makes our EGL context current, using the supplied surface for both "draw" and "read".
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    /**
     * Calls {@link EGL14#eglSwapBuffers}. Use this to "publish" the current frame.
     *
     * @return false on failure
     */
    public boolean swapBuffers() {
        boolean result = EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
        if (!result) {
            Log.e(TAG, "swapBuffers() failed");
        }
        return result;
    }

    /**
     * Releases any resources associated with the EGL surface.
     */
    public void release() {
        EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
        mEGLSurface = EGL14.EGL_NO_SURFACE;

        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.
            // So for every eglInitialize() we need an eglTerminate().
            EGL14.eglMakeCurrent(
                    mEGLDisplay,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(mEGLDisplay);
        }

        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLConfig = null;
    }

    /**
     * Finds a suitable EGLConfig.
     *
     * @param glEsVersion Must be 2 or 3.
     */
    private EGLConfig getConfig(int glEsVersion) {
        int renderableType = EGL14.EGL_OPENGL_ES2_BIT;
        if (glEsVersion >= 3) {
            renderableType |= EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }

        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.
        int[] attributes = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                // EGL14.EGL_DEPTH_SIZE, 16,
                // EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE
        };

        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(
                mEGLDisplay,
                attributes,
                0,
                configs,
                0,
                configs.length,
                numConfigs,
                0)) {
            Log.e(TAG, "unable to find RGB8888 / " + glEsVersion + " EGLConfig");
            return null;
        }
        return configs[0];
    }

    /**
     * Checks for EGL errors. Throws an exception if an error has been raised.
     */
    private static void checkEglError(String msg) {
        final int error = EGL14.eglGetError();
        if (error != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }
}