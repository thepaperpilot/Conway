package com.thepaperpilot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
		background = new LifeSimulator(MathUtils.ceil(Gdx.graphics.getWidth() / LifeSimulator.cellSize), new ArrayList<Vector>(), new ArrayList<Vector>());
		background.setFillParent(true);
		title = new Label("Conway's Game of Life\nThe Game", Conway.skin);
		title.setAlignment(Align.center);
		start = new TextButton("Play Game", Conway.skin);
		start.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				//game.setScreen(new GameScreen());
			}
		});

		items.add(title).padBottom(40).row();
		items.add(start);
	}

	@Override
	public void update(float delta) {
		background.step(delta);
	}

	@Override
	public void hide() {
		batch.dispose();
	}
}
