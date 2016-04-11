package com.zoneigh.clappybird;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.zoneigh.clappybird.model.IClapListener;
import com.zoneigh.clappybird.model.IGamer;
import com.zoneigh.clappybird.render.Box2dRendering;
import com.zoneigh.clappybird.render.SpriteRendering;

public class ClappyBird implements ApplicationListener {
	public static final float VIEWPORT_WIDTH = 48;
	public static final float VIEWPORT_HEIGHT = 80;
	public static final float TIMESTEP = 1 / 24f;
	
	private IGamer gamer;
	private IClapListener clapListener;
	
	private int screenWidth;
	private int screenHeight;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	public static World world;
	public static Box2DDebugRenderer renderer;
	
	public static int score;
	private int bestScore;

	public static boolean gameStarted;
	boolean gameEnded;
	public static boolean gamePlaying;
	
	private BitmapFont currentScoreFont;
	private BitmapFont bestScoreFont;
	private BitmapFont gameoverFont;
	private BitmapFont tapToRestartFont;
	private BitmapFont clapFont;
	
	public ClappyBird (IGamer gamer, IClapListener clapListener) {
		this.gamer = gamer;
		this.clapListener = clapListener;
		
		setBestScore(this.gamer.getBestScore());
		
		this.clapListener.startListening();
	}
		
	@Override
	public void create() {
		
		this.screenWidth = Gdx.graphics.getWidth();
		this.screenHeight = Gdx.graphics.getHeight();
		
		gameStarted = false;
		this.gameEnded = false;
		gamePlaying = false;
		
		this.batch = new SpriteBatch();
		this.currentScoreFont = new BitmapFont();
		this.bestScoreFont = new BitmapFont();
		this.gameoverFont = new BitmapFont();
		this.tapToRestartFont = new BitmapFont();
		this.clapFont = new BitmapFont();
		
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		Box2dRendering.createRendering();
		world = Box2dRendering.world;
		renderer = Box2dRendering.renderer;
		score = 0;
		
		SpriteRendering.createSprites();
	}

	@Override
	public void dispose() {
		batch.dispose();
		SpriteRendering.disposeSprites();
	}

	@Override
	public void render() {
		
		long currentTimestamp = System.currentTimeMillis();
		
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		renderer.render(world, camera.combined);
		
		if (this.clapListener.isManualAllowed() && Gdx.input.isTouched()) {
			this.clapListener.sendClap();
		}
		
		if (this.clapListener.hasClap() && !this.gameEnded) {
			
			// TODO debug stuff. remove later
			Gdx.gl.glClearColor(1, 0, 0, 1);
			
			if (!gamePlaying) {
				// Start the game
				gameStarted = true;
				gamePlaying = true;
				
        		Box2dRendering.createRendering();
        		world = Box2dRendering.world;
        		renderer = Box2dRendering.renderer;
				
				Box2dRendering.createFirstObstacles();

			} else {
				// Jump!
				Box2dRendering.jumpBird();
			}
			
			this.clapListener.acknowledgeClap();
		}
		
		if (this.gameEnded) {
			
			this.clapListener.stopListening();
			
			//tap to resume
			if (Gdx.input.isTouched()) {
				
				this.clapListener.startListening();
				
				this.gameEnded = false;
				gameStarted = false;
				gamePlaying = false;
        		Box2dRendering.createRendering();
        		world = Box2dRendering.world;
        		renderer = Box2dRendering.renderer;
			}
		}
		
		Box2dRendering.cycleObstacles();
		Box2dRendering.checkScore();
		
		//update best score automatically
		if (score > this.bestScore) {
			this.gamer.recordNewScore(score);
			setBestScore(this.gamer.getBestScore());
		}
		
		Box2dRendering.hoverBird(!gamePlaying && !gameStarted);
		
		world.step(Box2dRendering.timeStep, 6, 2);
		
		batch.begin();
		
		SpriteRendering.renderBackground(batch);
		
		//add all obstacles into a single list then render them
		ArrayList<Body> allObstacles = new ArrayList<Body>();
		for (Body body : Box2dRendering.lastObstacles) {
			allObstacles.add(body);
		}
		for (Body body : Box2dRendering.scoreObstacles) {
			allObstacles.add(body);
		}
		for (Body body : Box2dRendering.obstacleList) {
			allObstacles.add(body);
		}
		SpriteRendering.renderObstacles(batch, allObstacles);
		
		SpriteRendering.renderGround(batch);
		SpriteRendering.renderBird(batch);
		
		if (!gamePlaying) {
			if (!gameStarted) {
				// Game hasn't started
				SpriteRendering.renderStartScreenBanner(batch);
				
				if ((currentTimestamp / 800) % 2 == 0) { // Blink
					this.clapFont.setScale(3);
					this.clapFont.draw(batch, "Clap to begin", this.screenWidth - 400, (this.screenHeight * 3.0f / 5.0f));
				}
				
			} else {
				// Ended Game
				this.gamer.recordNewScore(score);
				setBestScore(this.gamer.getBestScore());
				score = 0;
				this.gameEnded = true;
				
				this.gameoverFont.setScale(8);
				this.gameoverFont.setColor(0.8f, 0.2f, 0, 1);
				this.gameoverFont.draw(batch, "Game Over!", 40, (this.screenHeight * 5.0f / 7.0f));
				
				if ((currentTimestamp / 800) % 2 == 0) { // Blink
					this.tapToRestartFont.setScale(3);
					this.tapToRestartFont.draw(batch, "tap to play again", this.screenWidth - 480, this.screenHeight - 500);
				}
			}
		}
		this.currentScoreFont.setScale(4);
		this.currentScoreFont.draw(batch, "" + score, 20, this.screenHeight - 20); // TODO relative position
		
		this.bestScoreFont.setScale(4);
		this.bestScoreFont.draw(batch, "Best: " + this.bestScore, this.screenWidth - 300, this.screenHeight - 20); // TODO relative position
		
		batch.end();

		// tell the camera to update its matrices.
		camera.update();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
//		Box2dRendering.timeStep = 0;
	}

	@Override
	public void resume() {
//		Box2dRendering.timeStep = TIMESTEP;
	}
	
	private void setBestScore (int bestScore) {
		this.bestScore = bestScore;
	}
	
}
