package com.coldtoast.circlejump;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Hank {
	private final Texture texture;
	private final Texture hatTexture;
	public final Sprite image;
	private final Sprite hat;
	public final Rectangle bounds;
	public final Vector2 v;
	public boolean dead;
	public boolean fadeIn = false;
	public float alpha = 0;
	private static Preferences outfit = Gdx.app.getPreferences("outfit");

	public Hank(){
		texture = new Texture(Gdx.files.internal("hank.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		hatTexture = new Texture(Gdx.files.internal("clothes/hats/" + getHat()));
		hatTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		image = new Sprite(texture);
		hat = new Sprite(hatTexture);
		hat.flip(false, true);
		image.flip(false, true);
		//bounds = new Rectangle(508,1344,64,64); //This is Hank's position for gameplay start
		bounds = new Rectangle(508,928,64,64); //This is Hank's position for intro
		image.setOriginCenter();
		image.setRotation((float)0);
		image.setAlpha(alpha);
		v = new Vector2(0,0);
		dead = false;
	}
	
	public void jump(){
		v.y -= 32 + (CircleJump.gravity * 7);
	}
	
	public SpriteBatch draw(SpriteBatch batch){
		image.setPosition(bounds.x, bounds.y);
		image.draw(batch);
		hat.setPosition(bounds.x, bounds.y);
		hat.draw(batch);
		
		return batch;
	}
	
	public void die(){
		dead = true;
		CircleJump.gravity = 0;
		CircleJump.hurt.play(0.5F);
		image.setRotation(-45);
		hat.setRotation(-45);
		v.x = -20;
		if(CircleJump.settings.getInteger("highScore") < CircleJump.currentScore){
			CircleJump.settings.putInteger("highScore", CircleJump.currentScore);
		}
		CircleJump.settings.putInteger("stars", CircleJump.settings.getInteger("stars") + CircleJump.currentScore / 5);
		CircleJump.settings.flush();
	}
	
	public String getHat(){
		if(outfit.getInteger("hat", 0) == 1){
			return "headband.png";
		}
		else{
			return "none.png";
		}
	}
}
