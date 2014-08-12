package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thepaperpilot.Input;

public abstract class ConwayScreen implements Screen {
	public SpriteBatch batch;
	public Stage stage;
	public Table items;

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		items = new Table();
		items.setFillParent(true);
		stage = new Stage(new ScreenViewport());
		stage.addActor(items);

		Gdx.input.setInputProcessor(new InputMultiplexer(Input.getInstance(), stage));
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void render(float delta) {
		update(delta);
		stage.draw();
	}

	public void update(float delta) {
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
	}
}
