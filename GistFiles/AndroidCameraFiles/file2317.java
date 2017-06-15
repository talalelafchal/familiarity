package com.theo5970.project01;

public class ObjectShape {
	public ShapeType type;
	public float[] verticles;
	public float width;
	public float height;

	public ObjectShape() {
		this.type = ShapeType.None;
		this.width = this.height = -1;

	}
	public ObjectShape(float width, float height) {
		this.type = ShapeType.Box;
		this.width = width;
		this.height = height;
	}
	public ObjectShape(boolean isCircle, float value) {
		this.type = isCircle ? ShapeType.Circle : ShapeType.Line;
		this.width = value;
		this.height = -1;
	}
	public ObjectShape(float[] verticles) {
		this.type = ShapeType.Polygon;
		this.width = this.height = -1;
		this.verticles = verticles;
	}
}
