package com.theo5970.project01;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by corep20 on 2016-12-24.
 */
public class Box2DBuilder {
    private static World world;
    private static Shape shapePool;


    // 물리월드를 설정한다.
    public static void init(World physicsWorld) {
        world = physicsWorld;
    }

    // 사각형 모양 객체를 생성한다.
    public static PolygonShape buildBoxShape(float width, float height) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        return shape;
    }

    // 다각형 모양 객체를 생성한다.
    public static PolygonShape buildPolygonShape(float[] verticles) {
        PolygonShape shape = new PolygonShape();
        shape.set(verticles);
        return shape;
    }

    // 원 모양 객체를 생성한다.
    public static CircleShape buildCircleShape(float radius) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        return shape;
    }

    // 몸체 정의객체를 생성한다.
    public static BodyDef buildBodyDef(BodyDef.BodyType bodyType, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);
        return bodyDef;
    }

    // 고정물 정의객체를 생성한다.
    public static FixtureDef buildFixtureDef(Shape shape, float density, float restitution, float friction) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;
        fixtureDef.shape = shape;
        shapePool = shape;
        return fixtureDef;
    }

    // 몸체 객체를 생성한다. (with 데이터 객체)
    public static Body buildBody(BodyDef bodyDef, FixtureDef fixtureDef, Object userData) {
        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shapePool.dispose();
        if (userData != null) body.setUserData(userData);
        return body;
    }

    // 몸체 객체를 생성한다.
    public static Body buildBody(BodyDef bodyDef, FixtureDef fixtureDef) {
        return buildBody(bodyDef, fixtureDef, null);
    }

    // 사각형 몸체 객체를 생성한다.
    public static Body buildBox(BodyDef.BodyType bodyType, float x, float y, float width, float height) {
        Body body = buildBody(buildBodyDef(bodyType, x, y),
                buildFixtureDef(buildBoxShape(width, height), 0.1f, 0.2f, 0.6f));
        body.setUserData(new GameObject(body, width, height));
        return body;
    }

    // 원형 몸체 객체를 생성한다.
    public static Body buildCircle(BodyDef.BodyType bodyType, float x, float y, float radius) {
        return buildBody(buildBodyDef(bodyType, x, y),
                buildFixtureDef(buildCircleShape(radius), 0.1f, 0.6f, 0.2f));
    }
}
