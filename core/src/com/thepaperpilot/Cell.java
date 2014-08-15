package com.thepaperpilot;

import com.badlogic.gdx.math.Vector2;

public class Cell {
	public final Vector2 pos;
	public boolean live = false;
	public boolean next = false;
	public boolean target = false;
	public int state = 6;
	/*
	States:
	white, toWhite2, toWhite1, gray, toBlack1, toBlack2, black
	white = 0, gray = 3, black = 6
	 */

	public Cell(Vector2 pos) {
		this.pos = pos;
	}

	public void update() {
		live = next;
	}

	public void setState(boolean live) {
		this.live = live;
		next = live;
		state = live ? 0 : 6;
	}
}
