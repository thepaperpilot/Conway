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
	public static final int cellSize = 200;
	private static ArrayList<Sprite> states;
	public final Cell[][] grid;
	public final Vector3 pan = new Vector3(0, 0, 0);
	private final SpriteBatch batch;
	public Vector2 size;
	public Objective objective;
	public float zoom = .1f;
	private boolean warping = true;
	private float time = 0;
	private int anim = 0;

	public GameOfLife(Vector2 size) {
		grid = new Cell[(int) size.x][(int) size.y];
		this.size = size;
		batch = new SpriteBatch();
		for(int i = 0; i < grid.length; i++)
			for(int i2 = 0; i2 < grid[i].length; i2++)
				grid[i][i2] = new Cell(new Vector2(i, i2));

		if(states == null) {
			states = new ArrayList<Sprite>();
			for(Sprite sprite : Conway.manager.get("states.atlas", TextureAtlas.class).createSprites()) {
				states.add(sprite);
			}
		}
	}

	public GameOfLife(Vector2 size, ArrayList<Vector2> initialCells, boolean warping) {
		this(size);
		this.warping = warping;
		objective = Objective.get(grid);
		for(Vector2 pos : initialCells) grid[((int) pos.x)][((int) pos.y)].live = true;
	}

	public GameOfLife(Vector2 size, ArrayList<Vector2> initialCells, boolean warping, boolean kill, ArrayList<Vector2> targets) {
		this(size);
		this.size = size;
		this.warping = warping;
		this.objective = Objective.get(kill, targets, grid);
		for(Vector2 pos : initialCells) grid[((int) pos.x)][((int) pos.y)].live = true;
	}

	public boolean update(float delta, boolean stepping, boolean fast) {
		boolean stepped = false;
		time += delta;
		while(time > .04f) {
			time -= .04f;
			updateStates();
			anim++;
			int speed = 16;
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

	public void draw(float alpha) {
		Matrix4 transform = new Matrix4();
		transform.translate((Gdx.graphics.getWidth() - (zoom * size.x * GameOfLife.cellSize)) / 2, (Gdx.graphics.getHeight() - (zoom * size.y * GameOfLife.cellSize)) / 2, 0);
		transform.scl(zoom);
		transform.translate(pan);
		batch.setTransformMatrix(transform);
		batch.begin();
		batch.setColor(1, 1, 1, alpha);
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
	}

	public void dispose() {
		batch.dispose();
	}

	public static class Objective {
		final Cell[][] grid;
		public String objective = "";

		public Objective(String objective, Cell[][] grid) {
			this.objective = objective;
			this.grid = grid;
		}

		public static Objective get(Cell[][] grid) {
			return new Objective("Kill the entire population", grid) {
				@Override
				public boolean checkCompletion() {
					for(Cell[] row : grid)
						for(Cell cell : row)
							if(cell.live)
								return false;
					return true;
				}
			};
		}

		public static Objective get(boolean kill, final ArrayList<Vector2> targets, Cell[][] grid) {
			for(Vector2 pos : targets) grid[((int) pos.x)][((int) pos.y)].target = true;
			if(kill)
				return new Objective("Kill all targets", grid) {
					@Override
					public boolean checkCompletion() {
						for(Vector2 pos : targets)
							if(grid[(int) pos.x][(int) pos.y].live)
								return false;
						return true;
					}
				};
			else
				return new Objective("Populate all targets", grid) {
					@Override
					public boolean checkCompletion() {
						for(Vector2 pos : targets)
							if(!grid[(int) pos.x][(int) pos.y].live)
								return false;
						return true;
					}
				};
		}

		public boolean checkCompletion() {
			return false;
		}
	}
}

