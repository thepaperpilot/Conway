package com.thepaperpilot.screens;

import com.badlogic.gdx.Game;
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
import com.thepaperpilot.LifeSimulator;

import java.util.ArrayList;

public class Menu extends ConwayScreen {
	LifeSimulator background;
	Label title;
	TextButton start;

	public Menu(final Game game) {
		super(game);
	}

	@Override
	public void show() {
		background = new LifeSimulator(new Vector2(MathUtils.ceil(Gdx.graphics.getWidth() / LifeSimulator.cellSize), MathUtils.ceil(Gdx.graphics.getHeight() / LifeSimulator.cellSize)), new ArrayList<Vector2>(), new ArrayList<Vector2>(), false);
		background.setFillParent(true);
		title = new Label("Conway's Game of Life\nThe Game", Conway.skin);
		title.setAlignment(Align.center);
		title.setColor(1, 0, 0, 1);
		start = new TextButton("Play Game", Conway.skin);
		start.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				//game.setScreen(new GameScreen());
			}
		});

		items.add(title).padBottom(40).row();
		items.add(start);

		stage.addActor(background);
		stage.getActors().reverse();

		for(int i = 0; i < 1000; i++) {
			Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
			cell.live = true;
		}
	}

	@Override
	public void update(float delta) {
		background.update(delta);
		if(background.checkEmpty())
			for(int i = 0; i < 1000; i++) {
				Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
				cell.live = true;
			}
	}

	@Override
	public void hide() {
		batch.dispose();
	}
}
