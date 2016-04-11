package com.zoneigh.clappybird.render;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.zoneigh.clappybird.ClappyBird;

public class SpriteRendering {
	
	private static Texture floorTexture;
	private static Sprite floorSprite;
	
	private static TextureRegion bottomObstacleTexture;
	private static TextureRegion topObstacleTexture;
	
	private static Texture spriteTexture;
	private static TextureRegion startScreenBanner;
	private static TextureRegion backgroundImage;
	private static TextureRegion[] birdTextures;
	private static int screenWidth;
	private static int screenHeight;
	
    private static float scaleY;
    private static float scaleX;
	private static Sprite birdSprite;
	private static Animation birdAnimation;
	private static float stateTime;
	private static TextureRegion currentBirdFrame;
	
	public static void createSprites() {		
		spriteTexture = new Texture(Gdx.files.internal("data/sprites.png"));
		
		//starts at (640, 256), size 318x133
		startScreenBanner = new TextureRegion(spriteTexture, 640, 256, 318, 133);
		//starts at (0, 0), size 640x960
		backgroundImage = new TextureRegion(spriteTexture, 0, 0, 640, 960);
		//starts at (640, 0), size 156x128
		birdTextures = new TextureRegion[3];
		birdTextures[0] = new TextureRegion(spriteTexture, 640, 0, 156, 128);
		//starts at (796, 0), size 156x128
		birdTextures[1] = new TextureRegion(spriteTexture, 796, 0, 156, 128);
		//starts at (640, 128), size 156x128 
		birdTextures[2] = new TextureRegion(spriteTexture, 640, 128, 156, 128);
		//starts at (796, 402), size 112x608
		bottomObstacleTexture = new TextureRegion(spriteTexture, 796, 402, 112, 608);
		//starts at (664, 402), size 108x608
		topObstacleTexture = new TextureRegion(spriteTexture, 664, 402, 108, 608);		
		
		birdAnimation = new Animation(0.075f, birdTextures);
		stateTime = 0f;
		
		birdSprite = new Sprite(birdTextures[0]);
		
		//Calculate birdSize
		//in Bird.java, I blew it up 5 times
		//it was 1 x 0.82, now it is 5 x 4.10
		//so width would be (5 / VIEWPORT_WIDTH) * screenWidth
		//height would be (4.10 / VIEWPORT_HEIGHT) * screenHeight
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		birdSprite.setSize(Math.round((5f / ClappyBird.VIEWPORT_WIDTH) * screenWidth), Math.round((4.1f / ClappyBird.VIEWPORT_HEIGHT) * screenHeight));
		scaleY = (float) screenHeight / ClappyBird.VIEWPORT_HEIGHT;
		scaleX = (float) screenWidth / ClappyBird.VIEWPORT_WIDTH;
		
		floorTexture = new Texture(Gdx.files.internal("data/floor.png"));
		floorTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		floorSprite = new Sprite(floorTexture, 0, 0, 128, 64);
		floorSprite.setSize(screenWidth, 64);
	}
	
	public static void disposeSprites() {
		spriteTexture.dispose();
		floorTexture.dispose();
	}
	
	public static void renderBackground(SpriteBatch batch) {
		batch.draw(backgroundImage, 0, 0, 0, 0, (float)screenWidth, (float)screenHeight, 1f, 1f, 0);
	}
	
	public static void renderGround(SpriteBatch batch) {
		//stateTime is shared by renderGround and renderBird
		stateTime += Gdx.graphics.getDeltaTime();
		//only scroll when the game is playing
		if (ClappyBird.gamePlaying || !ClappyBird.gameStarted) {
			floorSprite.setU(stateTime);
			floorSprite.setU2(stateTime + 2.25f);
		}
		
		batch.draw(floorSprite, 0, Math.round((960f - (759f + 64f)) / 960f * screenHeight), 0, 0, screenWidth, 64, 1, 1, 0);
	}
	
	public static void renderObstacles(SpriteBatch batch, ArrayList<Body> obstacles) {
		for (int i = 0; i < obstacles.size(); i++) {
			Body body = obstacles.get(i);
			if (i % 2 == 0) { //bottom obstacles
				float obsYPos = body.getPosition().y;
				float obstacleRatio = (obsYPos - Box2dRendering.FLOOR_HEIGHT) / Box2dRendering.OBSTACLE_HALF_WIDTH;
				int obstacleHeight = Math.round(112 * obstacleRatio);
				int obstacleWidth = 112;
				Sprite sprite = new Sprite(bottomObstacleTexture, 0, 0, obstacleWidth, obstacleHeight);
				sprite.setSize(obstacleWidth, obstacleHeight);
				sprite.setPosition(Math.round(body.getPosition().x / ClappyBird.VIEWPORT_WIDTH * screenWidth - (obstacleWidth / 2f)), Math.round(body.getPosition().y / ClappyBird.VIEWPORT_HEIGHT * screenHeight - (obstacleWidth * obstacleRatio / 2f)));
				sprite.draw(batch);
			} else { //top obstacles
				float obsYPos = body.getPosition().y;
				float obstacleRatio = (ClappyBird.VIEWPORT_HEIGHT - obsYPos) / Box2dRendering.OBSTACLE_HALF_WIDTH;
				int obstacleHeight = Math.round(108 * obstacleRatio);
				int obstacleWidth = 108;
				Sprite sprite = new Sprite(topObstacleTexture, 0, 608 - obstacleHeight, obstacleWidth, obstacleHeight);
				sprite.setSize(obstacleWidth, obstacleHeight);
				sprite.setPosition(Math.round(body.getPosition().x / ClappyBird.VIEWPORT_WIDTH * screenWidth - (obstacleWidth / 2f)), Math.round(body.getPosition().y / ClappyBird.VIEWPORT_HEIGHT * screenHeight - (obstacleWidth * obstacleRatio / 2f)));
				sprite.draw(batch);
			}
		}
	}
	
	public static void renderStartScreenBanner(SpriteBatch batch) {
		batch.draw(startScreenBanner, screenWidth / 2 - 318, screenHeight / 3 * 2, 0, 0, 318, 133, 2f, 2f, 0);
	}
	
	public static void renderBird(SpriteBatch batch) {
		currentBirdFrame = birdAnimation.getKeyFrame(stateTime, true);
		birdSprite.setRegion(currentBirdFrame);
	    Vector2 birdPos = Box2dRendering.birdBody.getPosition().sub(Box2dRendering.birdModelOrigin);
	    //scale it with screenHeight / VIEWPORT_HEIGHT
	    birdSprite.setPosition(birdPos.x * scaleX, birdPos.y * scaleY);
	    birdSprite.setOrigin(Box2dRendering.birdModelOrigin.x * scaleX, Box2dRendering.birdModelOrigin.y * scaleY);
	    birdSprite.setRotation(Box2dRendering.birdBody.getAngle() * MathUtils.radiansToDegrees);
	    birdSprite.draw(batch);
	}
}
