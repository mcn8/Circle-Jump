package com.coldtoast.circlejump;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
	
	Hank hank;
	Block block;
	MenuButton play;
	MenuButton logo;
	FreeTypeFontGenerator gen;
	FreeTypeFontParameter par;
	Sound jump;
	Sound hurt;
	Sound portal;
	Sound portalOpen;
	Sound portalClose;
	long portalId;
	
	double rotation = 0;
	double rotationSpeed = 2;
	boolean canScore = false;
	double resetSpeed = 0;
	
	boolean openPortal = false;
	int portalSize = 0;
	boolean closePortal = false;
	boolean inSession = false;
	boolean fontFadeIn = false;
	float fontAlpha = 0;
	
	int currentScore = 0;
	
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
		
		hank = new Hank();
		block = new Block(508,850);
		play = new MenuButton("play.png", 540, 960);
		logo = new MenuButton("logo.png", 540, 260);
	}

	

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.1F, 0.1F, 0.3F, 1F);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		block.bounds.setPosition((float)(Math.sin(Math.toRadians(rotation))*380 + 508), (float)(-Math.cos(Math.toRadians(rotation))*380 + 928));
		if(rotation+rotationSpeed >= 360){
			rotation = rotation+rotationSpeed - 360;
		} else {
			rotation += rotationSpeed;
		}
		
		if(play.fadeOut){
			if(play.alpha - 0.02F > 0){
				play.image.setAlpha(play.alpha-0.02F);
				play.alpha -= 0.02F;
				logo.image.setAlpha(logo.alpha-0.02F);
				logo.alpha-=0.02F;
			} else {
				play.alpha = 0;
				play.image.setAlpha(0);
				logo.alpha = 0;
				logo.image.setAlpha(0);
				play.fadeOut = false;
				openPortal = true;
				portalOpen.play(0.4F);
			}
		}
		
		if(openPortal){
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
		
		if(hank.fadeIn){
			
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
		
		if(closePortal){
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
		
		if(fontFadeIn){
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
		
		if(play.fadeIn){
			if(play.alpha + 0.02F < 1F){
				play.image.setAlpha(play.alpha+0.02F);
				play.alpha+=0.02F;
				logo.image.setAlpha(logo.alpha+0.02F);
				logo.alpha+=0.02F;
				rotationSpeed += resetSpeed;
			} else {
				play.alpha = 1F;
				play.image.setAlpha(1F);
				logo.alpha = 1F;
				logo.image.setAlpha(1F);
				play.fadeIn = false;
				hank.image.setRotation(0);
				hank.bounds.set(508, 928, 64, 64);
				hank.image.setAlpha(0);
				inSession = false;
				hank.dead = false;
				font.setColor(0, 1, 0, 0);
				currentScore = 0;
				rotationSpeed = 2;
				resetSpeed = 0;
			}
		}
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		hank.image.setPosition(hank.bounds.x, hank.bounds.y);
		hank.image.draw(batch);
		block.image.setPosition(block.bounds.x, block.bounds.y);
		block.image.draw(batch);
		font.draw(batch, Integer.toString(currentScore), 480, 850);
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
			
			if(!hank.dead && hank.bounds.overlaps(block.bounds)){
				hank.dead = true;
				hurt.play(0.5F);
				hank.image.setRotation(-45);
				hank.v.x = -20;
			}
			
			if(!hank.dead){
				if(hank.bounds.y + hank.v.y < 1344){
					hank.v.y += 1;
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
			
			if(!hank.dead && rotation > 200 && canScore){
				currentScore += 1;
				rotationSpeed += 0.15;
				canScore = false;
			}
		} else {
			if(play.isClicked(camera)){
				play.fadeOut = true;
			}
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
