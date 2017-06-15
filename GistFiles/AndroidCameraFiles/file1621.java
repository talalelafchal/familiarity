package com.theo5970.project01;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameObject {

	// Shape Information
	public ObjectShape shape;
	// Is Static
	public boolean isStatic;

	// Constructor (Extended)
	public GameObject(Body body, float width, float height) {
		this.shape = new ObjectShape(width, height);
		this.isStatic = body.getType() == BodyType.StaticBody;
	}

	// Constructor (Extended 2)
	public GameObject(Body body, float width, boolean isCircle) {
		this.shape = new ObjectShape(isCircle, width);
		this.isStatic = body.getType() == BodyType.StaticBody;
	}
}
