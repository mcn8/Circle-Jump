package com.coldtoast.circlejump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Block {
	private final Texture texture;
	public final Sprite image;
	public final Rectangle bounds;
	
	public Block(int x, int y){
		texture = new Texture(Gdx.files.internal("block.png"));
		image = new Sprite(texture);
		image.setOrigin(305,305);
		image.setPosition(240,660);
		bounds = new Rectangle(x, y, 64, 64);
	}
}
