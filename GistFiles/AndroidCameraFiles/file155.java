public class BasicGLRenderer implements GLSurfaceView.Renderer {

    ...
    private List<Model3D> mModels = new LinkedList<>();

    @Override
    public void onDrawFrame(GL10 gl) {
        ...
        for(Model3D model : mModels) {
            model.draw(mMVPMatrix);
        }
    }
    
    public void addModel(Model3D model3D) {
        mModels.add(model3D);
    }
}