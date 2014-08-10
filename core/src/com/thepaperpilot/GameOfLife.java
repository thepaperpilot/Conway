package com.thepaperpilot;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public class GameOfLife extends Table {
	public static int cellSize = 20;
	public static ArrayList<Sprite> states;
	private final Vector2 size;
	public Cell[][] grid;
	float time = 0;
	int anim = 0;
	int step = 0;
	private boolean warping;
	private ArrayList<Vector2> targets;
	//Used for replaying old games
	private ArrayList<Vector2> initialCells;
	private ArrayList<Move> moves;
	private float speed = .02f;

	public GameOfLife(Vector2 size, ArrayList<Vector2> targets, ArrayList<Vector2> initialCells, boolean warping) {
		this.warping = warping;
		this.size = size;
		grid = new Cell[(int) size.x][(int) size.y];
		for(int i = 0; i < grid.length; i++)
			for(int i2 = 0; i2 < grid[i].length; i2++)
				grid[i][i2] = new Cell(false, false, new Vector2(i, i2));

		for(Vector2 pos : targets) grid[((int) pos.x)][((int) pos.y)].target = true;
		for(Vector2 pos : initialCells) grid[((int) pos.x)][((int) pos.y)].live = true;

		if(states == null) {
			states = new ArrayList<Sprite>();
			for(Sprite sprite : Conway.manager.get("states.atlas", TextureAtlas.class).createSprites()) {
				states.add(sprite);
			}
		}
	}

	public void update(float delta) {
		time += delta;
		while(time > speed) {
			time -= speed;
			updateStates();
			anim++;
			if(anim == 5) {
				anim = 0;
				step();
			}
		}
	}

	private void step() {
		step++;
		Cell[][] next = grid.clone();
		for(Cell[] row : grid.clone()) { //making it a local variable speeds up the program
			for(Cell cell : row) {
				int neighbors = getNeighbors(cell);
				switch(neighbors) {
					default:
						next[(int) cell.pos.x][(int) cell.pos.y].live = false;
						break;
					case 3:
						next[(int) cell.pos.x][(int) cell.pos.y].live = true;
						break;
					case 2:
						break;
				}
			}
		}
		grid = next;
	}

	private void updateStates() {
		for(Cell[] row : grid) {
			for(Cell cell : row) {
				if(cell.live && cell.state != 0)
					cell.state--;
				if(!cell.live && cell.state != 6)
					cell.state++;
			}
		}
	}

	public boolean checkCompletion() {
		for(Vector2 pos : targets)
			if(grid[(int) pos.x][(int) pos.y].live)
				return false;
		return true;
	}

	public boolean checkEmpty() {
		for(Cell[] row : grid)
			for(Cell cell : row)
				if(cell.live) return false;
		return true;
	}

	private int getNeighbors(Cell cell) {
		int neighbors = 0;
		if(getNeighbor(cell, new Vector2(-1, 0))) neighbors++;
		if(getNeighbor(cell, new Vector2(1, 0))) neighbors++;
		if(getNeighbor(cell, new Vector2(0, -1))) neighbors++;
		if(getNeighbor(cell, new Vector2(0, 1))) neighbors++;
		if(getNeighbor(cell, new Vector2(1, 1))) neighbors++;
		if(getNeighbor(cell, new Vector2(-1, -1))) neighbors++;
		if(getNeighbor(cell, new Vector2(1, -1))) neighbors++;
		if(getNeighbor(cell, new Vector2(-1, 1))) neighbors++;
		return neighbors;
	}

	private boolean getNeighbor(Cell cell, Vector2 delta) {
		int x = (int) (cell.pos.x + delta.x);
		int y = (int) (cell.pos.y + delta.y);
		if((int) delta.x == -1) {
			if(cell.pos.x == 0)
				x = warping ? (int) size.x - 1 : -1;
		} else if((int) delta.x == 1) {
			if(cell.pos.x == (int) size.x - 1)
				x = warping ? 0 : -1;
		}
		if((int) delta.y == -1) {
			if(cell.pos.y == 0)
				y = warping ? (int) size.y - 1 : -1;
		} else if((int) delta.y == 1) {
			if(cell.pos.y == (int) size.y - 1)
				y = warping ? 0 : -1;
		}
		return !(x == -1 || y == -1) && grid[x][y].live;
	}

	public void draw(Batch batch, float parentAlpha) {
		for(int i = 0; i < grid.length; i++) {
			for(int i2 = 0; i2 < grid[i].length; i2++) {
				Cell cell = grid[i][i2];
				if(cell.target)
					batch.draw(states.get(7), i * cellSize, i2 * cellSize);
				batch.draw(states.get(cell.state), i * cellSize + 2, i2 * cellSize + 2);
			}
		}
	}

	public void toggle(Cell cell) {
		cell.live = !cell.live;
		moves.add(new Move(cell.pos.cpy(), step));
	}

	private class Move {
		public final Vector2 pos;
		public final int step;

		public Move(Vector2 pos, int step) {
			this.pos = pos;
			this.step = step;
		}
	}
}

