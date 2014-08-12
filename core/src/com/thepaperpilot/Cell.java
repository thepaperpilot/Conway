package com.thepaperpilot;

import com.badlogic.gdx.math.Vector2;

public class Cell {
	public Vector2 pos;
	public boolean live;
	public boolean target;
	public int state;
	public boolean next;
	/*
	States:
	white, toWhite2, toWhite1, gray, toBlack1, toBlack2, black
	white = 0, gray = 3, black = 6
	 */

	public Cell(boolean live, boolean target, Vector2 pos) {
		this.pos = pos;
		this.live = live;
		next = live;
		this.target = target;
		state = live ? 0 : 6;
	}

	public void update() {
		live = next;
	}
}
