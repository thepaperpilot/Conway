package com.thepaperpilot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public class LifeSimulator extends Table {
	float time = 0;

	public static int cellSize = 20;

	public LifeSimulator(int size, ArrayList<Vector> targets, ArrayList<Vector> initialCells) {
		//create grid
	}

	public void step(float delta) {
		time += delta;
		//do logic
	}

	public void render(SpriteBatch batch) {
		//render grid
	}

	public boolean toggle(Cell cell) {
		cell.live = !cell.live;
		return !cell.live;
	}
}

class Cell {
	public boolean live;
	public boolean target;

	public Cell(boolean live, boolean target) {
		this.live = live;
		this.target = target;
	}
}
