package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.thepaperpilot.Cell;
import com.thepaperpilot.GameOfLife;

import java.util.ArrayList;

public class GameScreen extends ConwayScreen implements GestureDetector.GestureListener {
	public final GameOfLife game;

	public GameScreen(GameOfLife game) {
		super();
		this.game = game;
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(new GestureDetector(this));
	}

	@Override
	public void show() {
	}

	public static void fillSquare(ArrayList<Vector2> cells, Vector2 pos, Vector2 size) {
		for(int i = (int) pos.x; i < pos.x + size.x; i++) {
			for(int i2 = (int) pos.y; i2 < pos.y + size.y; i2++) {
				cells.add(new Vector2(i, i2));
			}
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		game.draw();
		stage.draw();
	}

	public void update(float delta) {
		if(game.update(delta) && game.checkCompletion()) {
			//you win!
		}
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if(game.getBounds().contains(x, Gdx.graphics.getHeight() - y)) {
			for(Cell[] row : game.grid) {
				for(Cell cell : row) {
					if(game.getCellBounds(cell).contains(x, Gdx.graphics.getHeight() - y)) {
						game.toggle(cell);
						return true;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		game.pan.x = MathUtils.clamp(game.pan.x + (deltaX / game.zoom), -100 - (game.size.x * GameOfLife.cellSize) / 2, 100 + (game.size.x * GameOfLife.cellSize) / 2);
		game.pan.y = MathUtils.clamp(game.pan.y - (deltaY / game.zoom), -100 - (game.size.y * GameOfLife.cellSize) / 2, 100 + (game.size.y * GameOfLife.cellSize) / 2);
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		game.zoom = MathUtils.clamp(game.zoom + (distance - initialDistance) / 100000f, .1f, 1);
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void hide() {
		game.dispose();
		super.dispose();
	}
}