  import org.bytedeco.javacpp.opencv_videoio;  
    /**
     * Statically defined list of devices that opencv
     * supports as WebCam devices.
     */
    public enum SourceType {
        ANY(opencv_videoio.CV_CAP_ANY, "AutoDetect"),

        MIL(opencv_videoio.CV_CAP_MIL, "MIL proprietary drivers"),

        VFW(opencv_videoio.CV_CAP_VFW, "Platform Native"),
        V4L(opencv_videoio.CV_CAP_V4L, "Platform Native"),
        V4L2(opencv_videoio.CV_CAP_V4L2, "Platform Native"),

        FIREWARE(opencv_videoio.CV_CAP_FIREWARE, "IEEE 1394 drivers"),
        FIREWIRE(opencv_videoio.CV_CAP_FIREWIRE, "IEEE 1394 drivers"),
        IEEE1394(opencv_videoio.CV_CAP_IEEE1394, "IEEE 1394 drivers"),
        DC1394(opencv_videoio.CV_CAP_DC1394, "IEEE 1394 drivers"),
        CMU1394(opencv_videoio.CV_CAP_CMU1394, "IEEE 1394 drivers"),

        STEREO(opencv_videoio.CV_CAP_STEREO, "TYZX proprietary drivers"),
        TYZX(opencv_videoio.CV_CAP_TYZX , "TYZX proprietary drivers"),
        TYZX_LEFT(opencv_videoio.CV_TYZX_LEFT, "TYZX proprietary drivers"),
        TYZX_RIGHT(opencv_videoio.CV_TYZX_RIGHT, "TYZX proprietary drivers"),
        TYZX_COLOR(opencv_videoio.CV_TYZX_COLOR, "TYZX proprietary drivers"),
        TYZX_Z(opencv_videoio.CV_TYZX_Z, "TYZX proprietary drivers"),

        QT(opencv_videoio.CV_CAP_QT, "QuickTime"),

        UNICAP(opencv_videoio.CV_CAP_UNICAP, "Unicap drivers"),

        DSHOW(opencv_videoio.CV_CAP_DSHOW, "DirectShow (via videoInput)"),

        MSMF(opencv_videoio.CV_CAP_MSMF, "Microsoft Media Foundation (via videoInput)"),

        PVAPI(opencv_videoio.CV_CAP_PVAPI, "PvAPI, Prosilica GigE SDK"),

        OPENNI(opencv_videoio.CV_CAP_OPENNI, "OpenNI (for Kinect)"),

        OPENNI_ASUS(opencv_videoio.CV_CAP_OPENNI_ASUS, "OpenNI (for Asus Xtion)"),

        ANDROID(opencv_videoio.CV_CAP_ANDROID, "Android - not used"),
        ANDROID_BACK(opencv_videoio.CV_CAP_ANDROID_BACK, "Android back camera - not used"),
        ANDROID_FRONT(opencv_videoio.CV_CAP_ANDROID_FRONT, "Android front camera - not used"),

        XIAPI(opencv_videoio.CV_CAP_XIAPI, "XIMEA Camera API"),

        AVFOUNDATION(opencv_videoio.CV_CAP_AVFOUNDATION, "AVFoundation framework for iOS (OS X Lion will have the same API)"),

        GIGANETIX(opencv_videoio.CV_CAP_GIGANETIX, "Smartek Giganetix GigEVisionSDK"),

        INTELPERC(opencv_videoio.CV_CAP_INTELPERC, "Intel Perceptual Computing"),

        OPENNI2(opencv_videoio.CV_CAP_OPENNI2, "OpenNI2 (for Kinect)"),

        GPHOTO2(opencv_videoio.CV_CAP_GPHOTO2, "");

        /**
         * The constant value that this enum is equivalent to.
         */
        final int value;
        /**
         * The group that this input belongs to as defined by the comments in {@link opencv_videoio}.
         */
        final String group;
        SourceType(final int value, final String group){
            this.value = value;
            this.group = group;
        }
    }