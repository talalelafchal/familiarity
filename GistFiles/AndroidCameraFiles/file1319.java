package com.theo5970.project01;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MovingBody {
	public Body physicsBody;
	public float vx;
	public float vy;
	public Vector2 start;
	public Vector2 end;
	public MovingBody(Body body, float startX, float startY, float endX,
			float endY, float vx, float vy) {
		this.physicsBody = body;
		this.start = new Vector2(startX, startY);
		this.end = new Vector2(endX, endY);
		this.vx = vx;
		this.vy = vy;
		this.position = physicsBody.getPosition();
	}
	private Vector2 position;
	public void step() {
		position = physicsBody.getPosition();
		if (position.x >= end.x || position.x <= start.x) {
			vx *= -1;
		}
		if (position.y >= end.y || position.y <= start.y) {
			vy *= -1;
		}
		physicsBody.setLinearVelocity(vx, vy);
	}
}
