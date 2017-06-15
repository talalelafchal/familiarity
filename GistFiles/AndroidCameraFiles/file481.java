package com.oneunit.www.jumpingjohn;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Abbas on 11/29/2015.
 */
public abstract class PlayerBall extends AnimatedSprite {

    private Body body;
    private boolean canRun = false;
    private int footContacts = 0;
    private boolean isDead = false;
    private final float JUMP_HEIGHT = -9f;
    private final float DOUBLE_JUMP_HEIGHT = JUMP_HEIGHT*1.3f;
    private final float BOUNCE_FROM_SPRING_HEIGHT = -13.8f;
    private final float HORIZONTAL_SPEED = 6f;
    private final float HORIZONTAL_JUMP_SPEED = 3f;
    private final float ROTATION_STEP = 10;
    private final float JUMP_ROTATION_STEP = 5;

    private final int JUMP_OR_IDLE_TILE_INDEX = 0;
    private final int ROLL_TILE_INDEX = 1;
    public PlayerBall(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld){
        super(pX, pY, ResourceManager.getInstance().player_region, vbo);
        createPhysics(camera, physicsWorld);
        camera.setChaseEntity(this);
    }

    private void createPhysics(final Camera camera, PhysicsWorld physicsWorld){
        body = PhysicsFactory.createBoxBody(physicsWorld, this,
                BodyDef.BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
        body.setUserData("player_ball");
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                super.onUpdate(pSecondsElapsed);
                camera.onUpdate(0.1f);

                if (canRun) {
                    if (footContacts >= 1) {
                        setCurrentTileIndex(ROLL_TILE_INDEX);
                        setRotation(getNextRotation(getRotation()));
                        body.setLinearVelocity(new Vector2(HORIZONTAL_SPEED, body.getLinearVelocity().y));
                    } else {
                        setCurrentTileIndex(JUMP_OR_IDLE_TILE_INDEX);
                        setRotation(getNextJumpRotation(getRotation()));
                        body.setLinearVelocity(HORIZONTAL_JUMP_SPEED, body.getLinearVelocity().y);
                    }
                } else if (isDead) {
                    body.setLinearVelocity(0, 0);
                }

            }
        });
    }

    private float getNextRotation(float currentRotation){
        return currentRotation + ROTATION_STEP;
    }

    private float getNextJumpRotation(float currentRotation){

        return currentRotation + JUMP_ROTATION_STEP;
    }


    public void jump(){
        if(!shouldJump()){
            return;
        }

        body.setLinearVelocity(new Vector2(HORIZONTAL_JUMP_SPEED, JUMP_HEIGHT));
    }

    public void applyDoubleJump(){
        if(!shouldJump()){
            return;
        }
        body.setLinearVelocity(HORIZONTAL_JUMP_SPEED, DOUBLE_JUMP_HEIGHT);
    }

    public void bounceFromSpring(){
        if(!shouldJump()){
            return;
        }
        body.setLinearVelocity(HORIZONTAL_JUMP_SPEED, BOUNCE_FROM_SPRING_HEIGHT);
    }

    private boolean shouldJump(){
        if(!canRun){
            return false;
        }
        if(footContacts < 1){
            return false;
        }
        return true;
    }
    public void setRunning(){
        setCurrentTileIndex(ROLL_TILE_INDEX);
        canRun = true;
    }


    public void stopRunning(){
        setCurrentTileIndex(JUMP_OR_IDLE_TILE_INDEX);
        canRun = false;
    }

    public void increaseFootContacts(){
        this.footContacts++;
    }

    public void decreaseFootContacts(){
        this.footContacts--;
    }

    public void die(){
        canRun = false;
        isDead = true;
        final long[] PLAYER_ANIMATE = new long[]{100, 100, 100, 100, 100, 100};
        animate(PLAYER_ANIMATE, 0, 5, false);
    }



}
