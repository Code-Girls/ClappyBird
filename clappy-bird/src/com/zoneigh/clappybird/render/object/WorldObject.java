package com.zoneigh.clappybird.render.object;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class WorldObject {
	protected World world;
	protected Body body;
	protected BodyDef bodyDef;
	protected Shape shape;
	protected Fixture fixture;
	protected FixtureDef fixtureDef;
	
	public WorldObject(World world) {
		this.world = world;
		this.shape = null;
		this.bodyDef = null;
		this.fixtureDef = null;
		this.body = null;
		this.fixture = null;	
	}
	
	public WorldObject(World world, Shape shape, BodyDef bodyDef, FixtureDef fixtureDef) {
		this.initialize(world, shape, bodyDef, fixtureDef);
	}
	
	//may need this to initialize
	public void initialize(World world, Shape shape, BodyDef bodyDef, FixtureDef fixtureDef) {
		this.world = world;
		this.shape = shape;
		this.bodyDef = bodyDef;
		this.fixtureDef = fixtureDef;
		this.body = world.createBody(this.bodyDef);
		this.fixture = body.createFixture(this.fixtureDef);		
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Fixture getFixture() {
		return fixture;
	}

	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
	}
}
