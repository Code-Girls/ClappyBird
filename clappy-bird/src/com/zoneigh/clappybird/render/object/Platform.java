package com.zoneigh.clappybird.render.object;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Platform extends WorldObject {
	public Platform(World world, BodyType type, Vector2 initPos, float hx, float hy) {
		super(world);
		
		this.bodyDef = new BodyDef();
		this.bodyDef.type = type;
		this.bodyDef.position.set(initPos.x, initPos.y);
		
		this.body = this.world.createBody(this.bodyDef);
		
		this.shape = new PolygonShape();
		((PolygonShape) this.shape).setAsBox(hx, hy);

		this.fixtureDef = new FixtureDef();
		this.fixtureDef.shape = this.shape;
		this.fixtureDef.density = 0f;
		this.fixtureDef.friction = 0.5f;
		this.fixtureDef.restitution = 0f;
		
		this.fixture = this.body.createFixture(this.fixtureDef);
	}
	
	public Platform(World world, BodyType type, Vector2 center, float hx, float hy, float angle) {
		super(world);
		
		this.bodyDef = new BodyDef();
		this.bodyDef.type = type;
		
		this.body = this.world.createBody(this.bodyDef);
		
		this.shape = new PolygonShape();
		((PolygonShape) this.shape).setAsBox(hx, hy, center, angle);

		this.fixtureDef = new FixtureDef();
		this.fixtureDef.shape = this.shape;
		this.fixtureDef.density = 0f;
		this.fixtureDef.friction = 0.5f;
		this.fixtureDef.restitution = 0f;
		
		this.fixture = this.body.createFixture(this.fixtureDef);
	}
	
	public Platform(World world, BodyType type, Vector2 initPos, Vector2[] vectors) {
		super(world);
		
		this.bodyDef = new BodyDef();
		this.bodyDef.type = type;
		this.bodyDef.position.set(initPos.x, initPos.y);
		
		this.body = this.world.createBody(this.bodyDef);
		
		this.shape = new PolygonShape();
		((PolygonShape) this.shape).set(vectors);

		this.fixtureDef = new FixtureDef();
		this.fixtureDef.shape = this.shape;
		this.fixtureDef.density = 0f;
		this.fixtureDef.friction = 0.5f;
		this.fixtureDef.restitution = 0f;
		
		this.fixture = this.body.createFixture(this.fixtureDef);
	}
}
