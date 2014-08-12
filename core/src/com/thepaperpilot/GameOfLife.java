package com.thepaperpilot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class GameOfLife {
	public static int cellSize = 200;
	public static ArrayList<Sprite> states;
	public final Vector2 size;
	public Cell[][] grid;
	public float zoom = .1f;
	public Vector3 pan = new Vector3(0, 0, 0);
	SpriteBatch batch;
	float time = 0;
	int anim = 0;
	public int step = 0;
	private boolean warping;
	private ArrayList<Vector2> targets;
	//Used for replaying old games
	private ArrayList<Vector2> initialCells;
	private ArrayList<Move> moves = new ArrayList<Move>();
	private static int speed = 16;

	public GameOfLife(Vector2 size, ArrayList<Vector2> targets, ArrayList<Vector2> initialCells, boolean warping) {
		this.targets = targets;
		this.initialCells = initialCells;
		this.warping = warping;
		this.size = size;
		batch = new SpriteBatch();
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

	public boolean update(float delta, boolean stepping, boolean fast) {
		boolean stepped = false;
		time += delta;
		while(time > .04f) {
			time -= .04f;
			updateStates();
			anim++;
			if(stepping && anim >= (fast ? speed / 4 : speed)) {
				anim = 0;
				step();
				stepped = true;
			}
		}
		return stepped;
	}

	public void step() {
		for(Cell[] row : grid.clone()) { //making it a local variable speeds up the program
			for(Cell cell : row) {
				int neighbors = getNeighbors(cell);
				switch(neighbors) {
					default:
						grid[(int) cell.pos.x][(int) cell.pos.y].next = false;
						break;
					case 3:
						grid[(int) cell.pos.x][(int) cell.pos.y].next = true;
						break;
					case 2:
						break;
				}
			}
		}
		for(Cell[] row : grid) {
			for(Cell cell : row) {
				cell.update();
			}
		}
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
		if(checkNeighbor(cell, new Vector2(-1, 0))) neighbors++;
		if(checkNeighbor(cell, new Vector2(1, 0))) neighbors++;
		if(checkNeighbor(cell, new Vector2(0, -1))) neighbors++;
		if(checkNeighbor(cell, new Vector2(0, 1))) neighbors++;
		if(checkNeighbor(cell, new Vector2(1, 1))) neighbors++;
		if(checkNeighbor(cell, new Vector2(-1, -1))) neighbors++;
		if(checkNeighbor(cell, new Vector2(1, -1))) neighbors++;
		if(checkNeighbor(cell, new Vector2(-1, 1))) neighbors++;
		return neighbors;
	}

	private boolean checkNeighbor(Cell cell, Vector2 delta) {
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

	public Rectangle getBounds() {
		return new Rectangle((Gdx.graphics.getWidth() / 2) - (size.x * cellSize) / 2 * zoom + pan.x * zoom, (Gdx.graphics.getHeight() / 2) - (size.y * cellSize) / 2 * zoom + pan.y * zoom, cellSize * size.x * zoom, cellSize * size.y * zoom);
	}

	public Rectangle getCellBounds(Cell cell) {
		return new Rectangle((Gdx.graphics.getWidth() / 2) - (size.x * cellSize) / 2 * zoom + (pan.x + cellSize * cell.pos.x) * zoom, (Gdx.graphics.getHeight() / 2) - (size.y * cellSize) / 2 * zoom + (pan.y + cellSize * cell.pos.y) * zoom, cellSize * zoom, cellSize * zoom);
	}

	public void draw() {
		Matrix4 transform = new Matrix4();
		transform.translate((Gdx.graphics.getWidth() - (zoom * size.x * GameOfLife.cellSize)) / 2, (Gdx.graphics.getHeight() - (zoom * size.y * GameOfLife.cellSize)) / 2, 0);
		transform.scl(zoom);
		transform.translate(pan);
		batch.setTransformMatrix(transform);
		batch.begin();
		for(int i = 0; i < grid.length; i++) {
			for(int i2 = 0; i2 < grid[i].length; i2++) {
				Cell cell = grid[i][i2];
				if(cell.target)
					batch.draw(states.get(7), i * cellSize, i2 * cellSize);
				batch.draw(states.get(cell.state), i * cellSize, i2 * cellSize);
			}
		}
		batch.end();
	}

	public void toggle(Cell cell) {
		cell.live = !cell.live;
		cell.next = cell.live;
		moves.add(new Move(cell.pos.cpy(), step));
	}

	public void dispose() {
		batch.dispose();
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

