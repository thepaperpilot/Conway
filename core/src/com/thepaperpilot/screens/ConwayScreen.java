package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thepaperpilot.Conway;
import com.thepaperpilot.Input;

abstract class ConwayScreen implements Screen {
	Stage stage;
	Table items;
	TemporalAction transition;
	boolean reverse = false;
	private SpriteBatch batch;

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
		/*stage.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(0), Actions.scaleBy(2, 2), Actions.moveTo(-Gdx.graphics.getWidth(), -Gdx.graphics.getHeight())), Actions.parallel(transition = Actions.fadeIn(.5f), Actions.scaleBy(-2, -2, .5f), Actions.moveTo(0, 0, .5f)), Actions.run(new Runnable() {
			@Override
			public void run() {
				transition = null;
			}
		})));*/
		stage.addAction(Actions.sequence(Actions.fadeOut(0), transition = Actions.fadeIn(.5f), Actions.run(new Runnable() {
			@Override
			public void run() {
				transition = null;
			}
		})));

		Gdx.input.setInputProcessor(new InputMultiplexer(Input.getInstance(), stage));
	}

	void transition(final ConwayScreen screen) {
		reverse = true;
		/*stage.addAction(Actions.sequence(Actions.parallel(transition = Actions.fadeOut(.5f), Actions.scaleBy(-1, -1, .5f), Actions.moveTo(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, .5f)), Actions.run(new Runnable() {
			@Override
			public void run() {
				reverse = false;
				transition = null;
				Conway.getGame().setScreen(screen);
			}
		})));*/
		stage.addAction(Actions.sequence(transition = Actions.fadeOut(.5f), Actions.run(new Runnable() {
			@Override
			public void run() {
				reverse = false;
				transition = null;
				Conway.getGame().setScreen(screen);
			}
		})));
	}

	@Override
	public void hide() {
		batch.dispose();
		stage.dispose();
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
