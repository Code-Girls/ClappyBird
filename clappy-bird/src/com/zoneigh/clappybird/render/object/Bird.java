package com.zoneigh.clappybird.render.object;

import com.zoneigh.clappybird.render.Box2dRendering;
import com.zoneigh.clappybird.render.object.BodyEditorLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bird extends WorldObject {

	//TODO: Make a custom physics body (?)
	public Bird(World world, Vector2 initPos, Vector2 initVel, float radius) {
		super(world);

		this.bodyDef = new BodyDef();
		this.bodyDef.type = BodyType.DynamicBody;
		this.bodyDef.bullet = true;
		this.bodyDef.position.set(initPos.x, initPos.y);
		this.bodyDef.linearVelocity.set(initVel.x, initVel.y);

		this.body = this.world.createBody(this.bodyDef);

		this.shape = new CircleShape();
		this.shape.setRadius(radius);

		this.fixtureDef = new FixtureDef();
		this.fixtureDef.shape = this.shape;
		this.fixtureDef.density = 0.2f;
		this.fixtureDef.friction = 0.5f;
		this.fixtureDef.restitution = 0.2f;
		this.fixtureDef.isSensor = false;

		//this.fixture = this.body.createFixture(this.fixtureDef);
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/birdBody/bird.json"));
		loader.attachFixture(this.body, "birdBody", this.fixtureDef, 5f);
		Box2dRendering.birdModelOrigin = loader.getOrigin("birdBody", 5f).cpy();
	}

}