package com.example.game.game;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by peterijlst on 28-03-14.
 */

public class Game implements ApplicationListener {

    public PerspectiveCamera perspectiveCamera;
    public OrthographicCamera orthographicCamera;
    public Environment environment;
    public ModelBatch modelBatch;
    public ShapeRenderer shapeRenderer;
    public AssetManager assets;
    public FPSLogger fpsLog;
    public ModelInstance model;
    public CameraInputController camController;
    public boolean loading;
    public float sceneWidth;
    public float sceneHeight;
    public float gridHeight;
    public float gridWidth;

    // static variables
    public final static boolean fpsLogEnabled = false;
    public final static boolean camControllerEnabled = false;
    public final static boolean perspectiveCameraEnabled = true;
    public final static String BALL_MODEL = "data/ball.g3db";
    public final static String LOGTAG = "Game";

    /*
    Game Lifecycle
     */

    @Override
    public void create () {
        modelBatch = new ModelBatch();

        shapeRenderer = new ShapeRenderer();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float sceneAspect = height / width;

        sceneWidth = width;
        sceneHeight = sceneWidth * sceneAspect;
        gridHeight = sceneHeight / (10 * sceneAspect);
        gridWidth = sceneWidth / 10;

        if(perspectiveCameraEnabled){
            perspectiveCamera = new PerspectiveCamera(67,sceneWidth,sceneHeight);
            perspectiveCamera.position.set(0,0,10);
            perspectiveCamera.lookAt(0,0,0);
            perspectiveCamera.near = 0.1f;
            perspectiveCamera.far = 300f;
            perspectiveCamera.update();
        } else {
            orthographicCamera = new OrthographicCamera(sceneWidth,sceneHeight);
            orthographicCamera.position.set(sceneWidth/2,sceneHeight/2,10);
            orthographicCamera.lookAt(sceneWidth/2,sceneHeight/2,0);
            orthographicCamera.setToOrtho(false);
            orthographicCamera.update();
        }

        if(camControllerEnabled){
            if(perspectiveCameraEnabled){
                camController = new CameraInputController(perspectiveCamera);
            } else {
                camController = new CameraInputController(orthographicCamera);
            }
            Gdx.input.setInputProcessor(camController);
        }

        if(fpsLogEnabled){
            fpsLog = new FPSLogger();
        }

        assets = new AssetManager();
        assets.load(BALL_MODEL, Model.class);
        loading = true;
    }

    private void doneLoading() {
        Model ball = assets.get(BALL_MODEL, Model.class);
        model = new ModelInstance(ball);
        loading = false;
    }

    @Override
    public void render () {

        if (loading && assets.update()){
            doneLoading();
        }

        // update camera controller if enabled
        if(camControllerEnabled){
            camController.update();
        } else {
            // update configured camera
            if(perspectiveCameraEnabled){
                perspectiveCamera.update();
            } else {
                orthographicCamera.update();
            }
        }

        // set viewport
        Gdx.gl.glViewport(0, 0, (int)sceneWidth, (int)sceneHeight);

        // set rgba values used when clearing color buffer
        Gdx.gl.glClearColor(0, 0.2f, 0, 1);

        // clear color buffer and depth buffer
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // log FPS to console if enabled
        if(fpsLogEnabled){
            fpsLog.log();
        }

        // stop here if we don't have a modelInstance
        if(model == null){
            return;
        }

        // set line width
        Gdx.gl.glLineWidth(2);

        // begin render shapes with type line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // render X axis grid
        shapeRenderer.setColor(Color.RED);
        for(int i = 0; i <= sceneHeight; i+= gridHeight){
            shapeRenderer.line(
                    0,
                    i,
                    sceneWidth,
                    i
            );
        }
        // render Y axis grid
        shapeRenderer.setColor(Color.GREEN);
        for(int i = 0; i <= sceneWidth; i+= gridWidth){
            shapeRenderer.line(
                    i,
                    0,
                    i,
                    sceneHeight
            );
        }
        // stop render shapes
        shapeRenderer.end();

        // log corner world coordinates
        // logCornerCoordinates();

        // handle touch event
        if(Gdx.input.justTouched()){

            // create touch coordinates vector
            Vector3 touchPos = new Vector3();
            touchPos.x = Gdx.input.getX();
            touchPos.y = Gdx.input.getY();
            touchPos.z = 0;

            // convert screen coordinates to world coordinates
            if(perspectiveCameraEnabled){
                perspectiveCamera.unproject(touchPos);
            } else {
                orthographicCamera.unproject(touchPos);
            }

            // set Z to zero since it is not un projected correctly
            touchPos.z = 0;

            // set model transform to new vector
            model.transform.trn(touchPos);
        }

        // begin render modelBatch
        if(perspectiveCameraEnabled){
            modelBatch.begin(perspectiveCamera);
        } else {
            modelBatch.begin(orthographicCamera);
        }
        // render model and environment
        modelBatch.render(model, environment);
        // stop render modelBatch
        modelBatch.end();

    }

    public void resize (int width, int height) {
    }

    public void pause () {
    }

    public void resume () {
    }

    @Override
    public void dispose () {
        shapeRenderer.dispose();
        modelBatch.dispose();
        assets.dispose();
    }

    private void logCornerCoordinates () {

        Vector3 bottomleft = new Vector3();
        bottomleft.x = 0;
        bottomleft.y = 0;
        bottomleft.z = 0;

        Vector3 bottomright = new Vector3();
        bottomright.x = sceneWidth;
        bottomright.y = 0;
        bottomright.z = 0;

        Vector3 topleft = new Vector3();
        topleft.x = 0;
        topleft.y = sceneHeight;
        topleft.z = 0;

        Vector3 topright = new Vector3();
        topright.x = sceneWidth;
        topright.y = sceneHeight;
        topright.z = 0;

        if(perspectiveCameraEnabled){
            perspectiveCamera.unproject(bottomleft);
            perspectiveCamera.unproject(bottomright);
            perspectiveCamera.unproject(topleft);
            perspectiveCamera.unproject(topright);
        } else {
            orthographicCamera.unproject(bottomleft);
            orthographicCamera.unproject(bottomright);
            orthographicCamera.unproject(topleft);
            orthographicCamera.unproject(topright);
        }

        topright.z = 0;
        topleft.z = 0;
        bottomleft.z = 0;
        bottomright.z = 0;

        Log.d(LOGTAG, "bottomleft location unprojected: " + bottomleft.toString());
        Log.d(LOGTAG, "bottomright location unprojected: " + bottomright.toString());
        Log.d(LOGTAG, "topleft location unprojected: " + topleft.toString());
        Log.d(LOGTAG, "topright location unprojected: " + topright.toString());
    }
}