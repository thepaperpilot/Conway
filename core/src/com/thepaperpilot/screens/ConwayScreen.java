package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thepaperpilot.Conway;
import com.thepaperpilot.Input;

abstract class ConwayScreen implements Screen {
	Stage stage;
	Table items;

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		items = new Table();
		items.setFillParent(true);
		stage = new Stage(new ScreenViewport());
		stage.addActor(items);
		stage.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(0), Actions.scaleBy(2, 2), Actions.moveTo(-Gdx.graphics.getWidth(), -Gdx.graphics.getHeight())), Actions.parallel(Actions.fadeIn(.5f), Actions.scaleBy(-2, -2, .5f), Actions.moveTo(0, 0, .5f))));
		Gdx.input.setInputProcessor(new InputMultiplexer(Input.getInstance(), stage));
	}

	void transition(final ConwayScreen screen) {
		stage.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(.5f), Actions.scaleBy(-1, -1, .5f), Actions.moveTo(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, .5f)), Actions.run(new Runnable() {
			@Override
			public void run() {
				Conway.getGame().setScreen(screen);
			}
		})));
	}

	@Override
	public void hide() {
		//stage.dispose(); bugged. Keep it undisposed?
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
		stage.act(delta);
		stage.draw();
	}

	void update(float delta) {
	}

	@Override
	public void dispose() {
	}
}
