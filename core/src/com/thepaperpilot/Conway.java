package com.thepaperpilot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.thepaperpilot.screens.Title;

public class Conway extends Game {
	public static AssetManager manager = new AssetManager();

	public static Skin skin;
	private static Game instance;

	public Conway() {
		if(instance == null) instance = this;
	}

	public static Game getGame() {
		return instance;
	}

	@Override
	public void create() {
		setScreen(new Title());
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(.5f, .5f, .5f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
}
