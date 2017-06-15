package com.collosteam.bestbuttonsthe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created with IntelliJ IDEA.
 * User: Miroshnychenko Andre
 * Date: 12.11.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class MainMenuScreen implements Screen {

    Vector3 touchPos;

    final BaseGame game;

    OrthographicCamera camera;



    Skin skin;

    Stage stage;

    Table table;

    TextButton textButton;

    TextButton exitButton;

    Label label;


    public MainMenuScreen(final BaseGame gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        touchPos = new Vector3();

        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(gam.atlas);

        table = new Table(skin);

        table.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("buttonup");
        textButtonStyle.down = skin.getDrawable("buttondown");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = gam.font;



        textButton = new TextButton("PLAY" , textButtonStyle);
        textButton.pad(20f);

        textButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
              //  dispose();
            }
        }
        );

        exitButton = new TextButton("EXIT", textButtonStyle);
        exitButton.pad(20f);

        exitButton.addListener(new ClickListener(){

            @Override
            public void clicked (InputEvent event, float x, float y) {
                Gdx.app.exit();
            }

        });


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.font;

        label = new Label("The Best Buttons Game",labelStyle);
        label.setFontScale(1.1f);


        table.add(label).pad(10f);
        table.row();
        table.add(textButton).pad(20f);
        table.row();
        table.add(exitButton);


        stage.addActor(table);



//        button1 = new Skin(Gdx.files.internal("data/atlases/btns.atlas"),
//                new TextureAtlas(Gdx.files.internal("data/atlases/btns.png")));
//
//        imageButton = new ImageButton(button1.getDrawable("button"));


    }

    int i = 0;
    boolean key = false;
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Drop!!! ", 50, 100);
        game.font.draw(game.batch, "Tap anywhere to begin!", 50, 50);
        game.batch.end();
        stage.act(delta);
        stage.draw();

    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {

        skin.dispose();
        stage.dispose();

    }
}
