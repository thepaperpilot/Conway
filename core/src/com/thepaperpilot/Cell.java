package com.thepaperpilot;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Cell extends Button{
	public Vector2 pos;
	public boolean live = false;
	public boolean target = false;
	public int state = 6;
	/*
	States:
	white, toWhite2, toWhite1, gray, toBlack1, toBlack2, black
	white = 0, gray = 3, black = 6
	 */

	public Cell(Vector2 pos, final GameOfLife parent) {
		super(Conway.skin, "transparent");
		this.pos = pos;
		add(new Image(GameOfLife.states.get(state).getDrawable()));
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				parent.toggle(getSelf());
			}
		});
	}

	private Cell getSelf() {
		return this;
	}

	public void updateState() {
		clearChildren();
		add(new Image(GameOfLife.states.get(state).getDrawable()));
	}
}
