package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.Cell;
import com.thepaperpilot.Conway;
import com.thepaperpilot.GameOfLife;

import java.util.ArrayList;

public class Menu extends ConwayScreen {
	GameOfLife background;
	Label title;
	TextButton start;

	public Menu() {
		super();
		background = new GameOfLife(new Vector2(MathUtils.ceil(10 * Gdx.graphics.getWidth() / GameOfLife.cellSize), MathUtils.ceil(10 * Gdx.graphics.getHeight() / GameOfLife.cellSize)), new ArrayList<Vector2>(), new ArrayList<Vector2>(), false);
		title = new Label("Conway's Game of Life\nThe Game", Conway.skin);
		title.setAlignment(Align.center);
		title.setColor(1, 0, 0, 1);
		start = new TextButton("Play Game", Conway.skin);
		start.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Conway.getGame().setScreen(new GameScreen(random()));
			}
		});

		items.add(title).padBottom(40).row();
		items.add(start);

		stage.getActors().reverse();

		for(int i = 0; i < 1000; i++) {
			Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
			cell.live = true;
		}
	}

	@Override
	public void show() {
	}

	public GameOfLife random() {
		Vector2 size = new Vector2(MathUtils.random(15, 30), MathUtils.random(15, 30));
		int initial = MathUtils.random(100, 200);
		ArrayList<Vector2> initialCells = new ArrayList<Vector2>();
		ArrayList<Vector2> targets = new ArrayList<Vector2>();
		for(int i = 0; i < initial; i++)
			initialCells.add(new Vector2(MathUtils.random(size.x), MathUtils.random(size.y)));
		while(MathUtils.random(targets.size()) < 5)
			GameScreen.fillSquare(targets, new Vector2(MathUtils.random(size.x - 2), MathUtils.random(size.y - 2)), new Vector2(MathUtils.random(1, 2), MathUtils.random(1, 2)));
		return new GameOfLife(size, targets, initialCells, MathUtils.randomBoolean());
	}

	@Override
	public void update(float delta) {
		background.update(delta);
		if(background.checkEmpty())
			for(int i = 0; i < 1000; i++) {
				Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
				cell.live = true;
			}

		background.draw();
	}

	@Override
	public void hide() {
		batch.dispose();
		background.dispose();
	}
}
