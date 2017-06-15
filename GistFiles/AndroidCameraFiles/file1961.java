public class EasyGLView extends GLSurfaceView {

    public EasyGLView(Context context) {
        super(context);
        init();
    }

    public EasyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setEGLContextClientVersion(2);
        GLHelper.setContext(getContext().getApplicationContext());
    }
}