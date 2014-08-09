package com.thepaperpilot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class ConwayScreen implements Screen {
	public Game game;
	public SpriteBatch batch;
	public Stage stage;
	public Table items;

	public ConwayScreen(Game game) {
		this.game = game;
		batch = new SpriteBatch();
		stage = new Stage(new ScreenViewport());
		items = new Table();
		items.setFillParent(true);
		stage.addActor(items);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
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
	}
}
