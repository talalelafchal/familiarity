public class ExampleFragment extends Fragment {

    private EasyGLView mEasyGLView;
    private BasicGLRenderer mBasicRenderer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_example, container, false);
        mEasyGLView = (EasyGLView) viewGroup.findViewById(R.id.easygl);
        setupWorld();
        return viewGroup;
    }

    private void setupWorld() {
        GLCameraOptions glCameraOptions = new GLCameraOptions()
                .setEye(0, 0, -3.0f)
                .setUp(0, 1.0f, 0)
                .setDepthRange(0.5f, 100.0f);
        mBasicRenderer = new BasicGLRenderer(glCameraOptions);
        mBasicRenderer.setBackgroundColor(Color.BLUE);
        mEasyGLView.setRenderer(mBasicRenderer);
    }
}