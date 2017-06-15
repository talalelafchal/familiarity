    /* When initializing the ScannerSession, specify
     * that you wish to retrieve the query frames.
     */
    session.setExtras(Result.Extra.IMAGE);

    // (...)

    /* When you get a new Result, simply ask for the
     * frame!
     */

    /* This will get you the frame as physically provided
     * by the camera:
     */
    Bitmap frame = result.getImage();

    /* This will get you the frame as re-oriented by the
     * SDK, according to the `useDeviceOrientation` flag:
     */
    Bitmap o_frame = result.getOrientedImage();