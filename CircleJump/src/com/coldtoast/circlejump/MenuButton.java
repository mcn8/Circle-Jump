package com.coldtoast.circlejump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public class MenuButton {
	private final Texture texture;
	private Vector3 touch = new Vector3(0,0,0);
	
	public final Sprite image;
	public boolean fadeOut = false;
	public boolean fadeIn = false;
	public float alpha = 1F;
	
	public MenuButton(String textureName, int originX, int originY){
		texture = new Texture(Gdx.files.internal(textureName));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		image = new Sprite(texture);
		image.flip(false, true);
		image.setPosition(originX - image.getWidth()/2, originY - image.getHeight()/2);
	}
	
	public boolean isClicked(OrthographicCamera camera){
		//System.out.println("X: " + Gdx.input.getX() + " unprojected X: " + touch.x + " Width: " + image.getWidth() + " Image X: " + image.getX());
		if(Gdx.input.isTouched()){
			touch.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(touch);
			return touch.x >= image.getX() && touch.x < image.getX() + image.getWidth() && touch.y >= image.getY() && touch.y < image.getY() + image.getHeight();
		}
		
		return false;
	}
	

}
