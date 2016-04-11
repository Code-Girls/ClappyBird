package com.zoneigh.clappybird.render;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.zoneigh.clappybird.ClappyBird;
import com.zoneigh.clappybird.render.object.Bird;
import com.zoneigh.clappybird.render.object.Platform;

public class Box2dRendering {
	public static World world;
	public static FPSLogger logger;
	public static Box2DDebugRenderer renderer;
	public static float timeStep = ClappyBird.TIMESTEP;
	public static Vector2 birdModelOrigin;
	public static final float FLOOR_HEIGHT = 15f; 
	public static final float OBSTACLE_HALF_WIDTH = 3.5f;
	public static Body birdBody;
	
	
	public static ArrayList<Body> scoreObstacles;
	public static ArrayList<Body> lastObstacles;
	public static ArrayList<Body> obstacleList;
	//private static Body ground;
	//private static Fixture groundFixture;
	//private static Fixture circleFixture;

	public static void createRendering() {
		world = new World(new Vector2(0, -30f), true);
		World.setVelocityThreshold(150f);
		createContactListener();
		
		obstacleList = new ArrayList<Body>();
		scoreObstacles = new ArrayList<Body>();
		lastObstacles = new ArrayList<Body>();

		renderer = new Box2DDebugRenderer();
		logger = new FPSLogger();

		createBird();
		createGround();

	}
	
	public static void createContactListener() {
		//set collision detection, pretty much just resets the game for now
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Gdx.app.log("beginContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
                
                ClappyBird.gamePlaying = false;
                
                //stop all obstacles
                for (Body body : obstacleList) {
                	body.setLinearVelocity(0, 0);
                }
                for (Body body : scoreObstacles) {
                	body.setLinearVelocity(0, 0);
                }
                for (Body body : lastObstacles) {
                	body.setLinearVelocity(0, 0);
                }
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Gdx.app.log("endContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
	}

	public static void jumpBird() {
		//only jumps within the screen
		if (birdBody.getPosition().y <= ClappyBird.VIEWPORT_HEIGHT - 5f) {
			birdBody.setLinearVelocity(0, 22f);
		}
	}
	
	public static void hoverBird(boolean hovering) {
		if (hovering) {
			float currentYSpeed = birdBody.getLinearVelocity().y;
			if (currentYSpeed <= -30f) {
				birdBody.setLinearVelocity(0, -currentYSpeed + 1.25f);
			}
		}
	}
	
	public static void checkScore() {
		if (!scoreObstacles.isEmpty() && (birdBody.getPosition().x >= scoreObstacles.get(0).getPosition().x)) {
			lastObstacles.add(scoreObstacles.get(0));
			lastObstacles.add(scoreObstacles.get(1));
			//Remove scored obstacles
			scoreObstacles.remove(0);
			scoreObstacles.remove(0);
			//increment score
			ClappyBird.score++;
		}
	}

	private static void createBird() {
		Bird bird = new Bird(world, new Vector2(ClappyBird.VIEWPORT_WIDTH * 0.25f, ClappyBird.VIEWPORT_HEIGHT * 2f / 3f), new Vector2(0f, 0f), 1f);
		birdBody = bird.getBody();
	}

	private static void createGround() {
		new Platform(world, BodyType.StaticBody, new Vector2(0, FLOOR_HEIGHT / 2f), ClappyBird.VIEWPORT_WIDTH, FLOOR_HEIGHT / 2f);
	}
	
	public static void cycleObstacles() {
		if (!obstacleList.isEmpty()) {
			Body firstObstacle = obstacleList.get(0);
			if (firstObstacle.getPosition().x < ClappyBird.VIEWPORT_WIDTH * 0.55f) {
				createObstacles();
				scoreObstacles.add(firstObstacle);
				scoreObstacles.add(obstacleList.get(1));
				//Remove first 2 obstacles
				obstacleList.remove(0);
				obstacleList.remove(0);
			}
			if (firstObstacle.getPosition().x < -OBSTACLE_HALF_WIDTH) {
				for (Body body : lastObstacles) {
					world.destroyBody(body);
				}
				//Remove the last obstacles
				lastObstacles.remove(0);
				lastObstacles.remove(0);
			}
		}
	}
	
	public static void createObstacles() {
		float availSpace = (ClappyBird.VIEWPORT_HEIGHT - FLOOR_HEIGHT);
		float gap = 20f;
		float gapHeight = gap * 0.5f + (float)(Math.random() * (((availSpace - gap * 1.5f) - gap * 0.5f) + 1));
		Body bottomObstacle = createObstacle(gapHeight, true);
		Body topObstacle = createObstacle(availSpace - gapHeight - gap, false);
		
		obstacleList.add(bottomObstacle);
		obstacleList.add(topObstacle);
				
	}
	
	public static void createFirstObstacles() {
		createObstacles();
		for (Body body : obstacleList) {
			body.setTransform(body.getPosition().x + 35f, body.getPosition().y, 0);
		}
	}

	private static Body createObstacle(float height, boolean isBottom) {
		
		float xCenter = ClappyBird.VIEWPORT_WIDTH + OBSTACLE_HALF_WIDTH;
		float yCenter = isBottom ? FLOOR_HEIGHT + height / 2f : ClappyBird.VIEWPORT_HEIGHT - height / 2f;
		Vector2[] vectors = { new Vector2(-OBSTACLE_HALF_WIDTH, -(height / 2f)), new Vector2(OBSTACLE_HALF_WIDTH, -(height / 2f)),
				new Vector2(OBSTACLE_HALF_WIDTH, height / 2f), new Vector2(-OBSTACLE_HALF_WIDTH, height / 2f) };
		Platform obstacle = new Platform(world, BodyType.KinematicBody, new Vector2(xCenter, yCenter), vectors);
		obstacle.getBody().setLinearVelocity(-7.5f, 0f);
		return obstacle.getBody();
	}
}
