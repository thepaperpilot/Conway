package com.thepaperpilot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.thepaperpilot.GameOfLife;

import java.util.ArrayList;

public class GameScreen extends ConwayScreen implements GestureDetector.GestureListener{
	private GameOfLife game;

	public GameScreen(Game game) {
		super(game);
		ArrayList<Vector2> targets = new ArrayList<Vector2>();
		ArrayList<Vector2> initialCells = new ArrayList<Vector2>();

		fillSquare(targets, new Vector2(2, 2), new Vector2(5, 5));
		fillSquare(initialCells, new Vector2(0, 0), new Vector2(2, 2));

		this.game = new GameOfLife(new Vector2(10, 6), targets, initialCells, true);
		this.game.setFillParent(true);
		stage.addActor(this.game);
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(new GestureDetector(this));
		//Gdx.input.setInputProcessor(new GestureDetector(this));
	}

	public void fillSquare(ArrayList<Vector2> cells, Vector2 pos1, Vector2 pos2) {
		for(int i = (int) pos1.x; i <= pos2.x - pos1.x; i++) {
			for(int i2 = (int) pos1.y; i2 <= pos2.y - pos1.y; i2++) {
				cells.add(new Vector2(i, i2));
			}
		}
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
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
}
