package com.collosteam.bestbuttonsthe;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    Texture dropImage;
    Texture bucketImage;
    Texture closeImage;
    Sound dropSound;
    Music rainMusic;

    OrthographicCamera camera;
   // SpriteBatch batch;

    Rectangle bucket;

    Vector3 touchPos;

    Array raindrops;

    long lastDropTime;

    int dropsGathered;

    BaseGame mBaseGame;

    public GameScreen(final BaseGame baseGame) {
        mBaseGame = baseGame;


        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("data/droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("data/bucket.png"));
        closeImage = new Texture(Gdx.files.internal("data/close.png"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("data/capcap.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("data/rain.mp3"));

        // start the playback of the background music immediately
        rainMusic.setLooping(true);
        rainMusic.play();


        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1230, 720);

      //  batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 1230 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        touchPos = new Vector3();

        raindrops = new Array();
        spawnRaindrop();


    }



    @Override
    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void show() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    Rectangle closeRect = new Rectangle();


    @Override
    public void render(float delta) {

        closeRect.set((1230 - closeImage.getWidth()-20),(720 - closeImage.getHeight()-20),closeImage.getWidth(),closeImage.getHeight());

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera.update();

        mBaseGame.batch.setProjectionMatrix(camera.combined);
        mBaseGame.batch.begin();
        mBaseGame.font.draw(mBaseGame.batch, "Drops Collected: " + dropsGathered, 0, 720);
        mBaseGame.batch.draw(closeImage,  (1230 - closeImage.getWidth()-20) ,(720 - closeImage.getHeight()-20));
        mBaseGame.batch.draw(bucketImage, bucket.x, bucket.y);
        for(Object raindrop: raindrops) {
            mBaseGame.batch.draw(dropImage, ((Rectangle) raindrop).x, ((Rectangle) raindrop).y);
        }
        mBaseGame.batch.end();

        if(Gdx.input.isTouched()) {

            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if(closeRect.contains(touchPos.x,touchPos.y)){

                dispose();
                mBaseGame.setScreen(new MainMenuScreen(mBaseGame));

            }

            bucket.x = touchPos.x - 64 / 2;


        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        if(bucket.x < 0) bucket.x = 0;
        if(bucket.x > 1230 - 64) bucket.x = 1230 - 64;


        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        Iterator iter = raindrops.iterator();
        while(iter.hasNext()) {
            Rectangle raindrop = (Rectangle)iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0) iter.remove();


        if(raindrop.overlaps(bucket)) {
            dropsGathered++;
            dropSound.play();
            iter.remove();
        }
    }
    }

    @Override
    public void pause() {
        new MainMenuScreen(mBaseGame);
        dispose();
    }

    @Override
    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        closeImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    //    batch.dispose();

    }



    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 1230 - 64);
        raindrop.y = 720;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

}
