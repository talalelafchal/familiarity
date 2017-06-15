// We need to do this with OpenGL ES (*not* Canvas -- the "software render" bits
// are sticky). We can't stay connected to the Surface after we're done because
// that will prevent the camera from attaching.
// @param surface should be either Surface or SurfaceTexture
public static void clear(object surface) {
    try {
        if (Log.IS_DEBUG) Log.logDebug(TAG, "clear() using OpenGL ES: E");
        WindowSurface windowSurface = new WindowSurface(surface);
        windowSurface.makeCurrent();
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        windowSurface.swapBuffers();
        windowSurface.release();
        if (Log.IS_DEBUG) Log.logDebug(TAG, "clear() using OpenGL ES: X");
    } catch (RuntimeException exc) {
        Log.e(TAG, "clear() using OpenGL ES failed.", exc);
    }
}