package com.coldtoast.circlejump;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Hank {
	private final Texture texture;
	public final Sprite image;
	public final Rectangle bounds;
	public final Vector2 v;
	public boolean dead;
	public boolean fadeIn = false;
	public float alpha = 0;

	public Hank(){
		texture = new Texture(Gdx.files.internal("hank.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		image = new Sprite(texture);
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
		v.y -= 20;
	}
}
