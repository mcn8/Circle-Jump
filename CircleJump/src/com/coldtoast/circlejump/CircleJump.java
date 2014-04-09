package com.coldtoast.circlejump;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;

public class CircleJump implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private BitmapFont highFont;
	public static Preferences settings;
	
	Hank hank;
	Block block;
	MenuButton play;
	MenuButton logo;
	BackgroundSpinner bs1;
	BackgroundSpinner bs2;
	BackgroundSpinner bs3;
	FreeTypeFontGenerator gen;
	FreeTypeFontParameter par;
	Sound jump;
	static Sound hurt;
	Sound portal;
	Sound portalOpen;
	Sound portalClose;
	long portalId;
	
	double rotation = 0;
	double rotationSpeed = 2;
	boolean canScore = false;
	double resetSpeed = 0;
	static float gravity = 0;
	
	boolean openPortal = false;
	int portalSize = 0;
	boolean closePortal = false;
	boolean inSession = false;
	boolean fontFadeIn = false;
	float fontAlpha = 0;
	
	static int currentScore = 0;
	
	@Override
	public void create() {				
		camera = new OrthographicCamera();
		camera.setToOrtho(true, 1080, 1920);
		
		batch = new SpriteBatch();
		
		shapeRenderer = new ShapeRenderer();
		
		jump = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
		hurt = Gdx.audio.newSound(Gdx.files.internal("hurt.wav"));
		portal = Gdx.audio.newSound(Gdx.files.internal("portal.wav"));
		portalOpen = Gdx.audio.newSound(Gdx.files.internal("portalOpen3.wav"));
		portalClose = Gdx.audio.newSound(Gdx.files.internal("portalClose.wav"));
		
		gen = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		//par = new FreeTypeFontParameter();
		font = gen.generateFont(256);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font.setColor(0,1,0,fontAlpha);
		font.setScale(1,-1);
		
		highFont = gen.generateFont(80);
		highFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		highFont.setColor(.9F, .1F,0,1);
		highFont.setScale(1,-1);
		
		hank = new Hank();
		block = new Block(508,850);
		play = new MenuButton("squarePlayBig.png", 540, 960);
		logo = new MenuButton("logo.png", 540, 260);
		bs1 = new BackgroundSpinner();
		bs2 = new BackgroundSpinner();
		bs3 = new BackgroundSpinner();
		
		settings = Gdx.app.getPreferences("settings");
	}

	

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.1F, 0.1F, 0.3F, 1F);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		//set block position
		block.bounds.setPosition((float)(Math.sin(Math.toRadians(rotation))*380 + 508), (float)(-Math.cos(Math.toRadians(rotation))*380 + 928));
		if(rotation+rotationSpeed >= 360){
			rotation = rotation+rotationSpeed - 360;
		} else {
			rotation += rotationSpeed;
		}
		
		
		//Play has been clicked. Game intro initiate
		if(play.fadeOut){
			fadeOutPlay();
		}
		if(openPortal){
			openPortalFunction();
		}
		if(hank.fadeIn){
			fadeHankIn();
		}
		if(closePortal){
			closePortalFunction();
		}
		if(fontFadeIn){
			fadeFontIn();
		}
		
		
		//Hank has died. Fade back play button
		if(play.fadeIn){
			fadePlayIn();
		}
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		bs1.image.rotate(0.23F);
		bs2.image.rotate(0.05F);
		bs3.image.rotate(0.1F);
		bs1.image.setAlpha(0.2F);
		bs2.image.setAlpha(0.15F);
		bs3.image.setAlpha(0.25F);
		bs1.image.setPosition(-200, -250);
		bs2.image.setPosition(800, 500);
		bs3.image.setPosition(0, 1400);
		bs1.image.draw(batch);
		bs2.image.draw(batch);
		bs3.image.draw(batch);
		//hank.image.setPosition(hank.bounds.x, hank.bounds.y);
		//hank.image.draw(batch);
		batch = hank.draw(batch);
		block.image.setPosition(block.bounds.x, block.bounds.y);
		block.image.draw(batch);
		font.draw(batch, Integer.toString(currentScore), 480, 850);
		highFont.draw(batch, "High Score: " + settings.getInteger("highScore", 0), 220,1650);
		highFont.draw(batch, "Stars: " + settings.getInteger("stars", 0), 280, 1750);
		play.image.draw(batch);
		logo.image.draw(batch);
		batch.end();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		 
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1,1,0,1);
		shapeRenderer.circle(540, 960, 450);
		shapeRenderer.end();
		
		
		if(inSession){
			if(rotation > 0 && rotation < 190){
				canScore = true;
			}
			
			//check if Hank hit block
			if(!hank.dead && hank.bounds.overlaps(block.bounds)){
				hank.die();
				
			}
			
			//If Hank isn't dead, check his jumping speed etc.
			if(!hank.dead){
				if(hank.bounds.y + hank.v.y < 1344){
					hank.v.y += 2.3F + gravity;
				} else	{
					hank.v.y = 0;
					hank.bounds.y = 1344;
				}
			} else {
				hank.bounds.x += hank.v.x;
				if(hank.v.x < 0){
					hank.v.x += 1;
				} else {
					hank.v.x = 0;
				}
				hank.v.y = 5;
				if(hank.bounds.y > 1920){
					play.fadeIn = true;
					if(resetSpeed == 0)
						resetSpeed = (2 - rotationSpeed)/50;
				}
			}
			
			hank.bounds.y += hank.v.y;
	
			if(!hank.dead && Gdx.input.isTouched() && hank.bounds.y == 1344){
				hank.jump();
				jump.play(0.3F);
			}
			
			if(!hank.dead && rotation > 210 && canScore){
				currentScore += 1;
				rotationSpeed += 0.15;
				gravity += 0.1F;
				canScore = false;
			}
		} else {
			if(play.isClicked(camera)){
				play.fadeOut = true;
			}
		}
	}
	
	public void fadeOutPlay(){
		if(play.alpha - 0.02F > 0){
			play.image.setAlpha(play.alpha-0.02F);
			play.alpha -= 0.02F;
			logo.image.setAlpha(logo.alpha-0.02F);
			logo.alpha-=0.02F;
			highFont.setColor(.9F, .1F, 0, play.alpha);
		} else {
			play.alpha = 0;
			play.image.setAlpha(0);
			logo.alpha = 0;
			logo.image.setAlpha(0);
			highFont.setColor(.9F, .1F,0,0);
			play.fadeOut = false;
			openPortal = true;
			portalOpen.play(0.4F);
		}
	}
	
	public void openPortalFunction(){
		if(!(portalSize + 2 >= 100)){
			portalSize += 2;
		} else {
			hank.fadeIn = true;
			openPortal = false;
		}
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		 
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1,1,1,1);
		shapeRenderer.rect(590 - portalSize,910,portalSize,100);
		shapeRenderer.end();
	}
	
	public void fadeHankIn(){
		shapeRenderer.setProjectionMatrix(camera.combined);
		 
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1,1,1,1);
		shapeRenderer.rect(490,910,100,100);
		shapeRenderer.end();
		
		if(hank.alpha + 0.02F < 1F){
			hank.image.setAlpha(hank.alpha+0.02F);
			hank.alpha+=0.02F;
		} else {
			hank.alpha = 1F;
			hank.image.setAlpha(1F);
			if(rotation > 200 && rotation < 340){
				hank.fadeIn = false;
				inSession = true;
				closePortal = true;
				portalClose.play(0.4F);
			}
		}
	}
	
	public void closePortalFunction(){
		if(!(portalSize - 2 <= 0)){
			portalSize -= 2;
		} else {
			closePortal = false;
			fontFadeIn = true;
		}
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		 
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1,1,1,1);
		shapeRenderer.rect(490,910,portalSize,100);
		shapeRenderer.end();
	}
	
	public void fadeFontIn(){
		if(fontAlpha + 0.01F < 1F){
			fontAlpha += 0.01F;
			font.setColor(0, 1, 0, fontAlpha);
			portal.setLooping(portalId, false);
		} else {
			fontAlpha = 1F;
			font.setColor(0,1,0,fontAlpha);
			fontFadeIn = false;
		}
	}
	
	public void fadePlayIn(){
		if(play.alpha + 0.02F < 1F){
			play.image.setAlpha(play.alpha+0.02F);
			play.alpha+=0.02F;
			logo.image.setAlpha(logo.alpha+0.02F);
			logo.alpha+=0.02F;
			highFont.setColor(.9F, .1F, 0, play.alpha);
			rotationSpeed += resetSpeed;
		} else {
			play.alpha = 1F;
			play.image.setAlpha(1F);
			logo.alpha = 1F;
			logo.image.setAlpha(1F);
			highFont.setColor(.9F, .1F, 0, 1);
			play.fadeIn = false;
			hank.image.setRotation(0);
			hank.bounds.set(508, 928, 64, 64);
			hank.image.setAlpha(0);
			inSession = false;
			hank.dead = false;
			font.setColor(0, 1, 0, 0);
			currentScore = -1;
			rotationSpeed = 2;
			resetSpeed = 0;
		}
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
