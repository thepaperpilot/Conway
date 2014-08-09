package com.thepaperpilot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.thepaperpilot.Conway;

public class Title extends ConwayScreen {
	TextureRegion title;
	Label instructions;
	float time = 0;

	public Title(Game game) {
		super(game);
	}

	@Override
	public void show() {
		//Load title screen assets
		Conway.manager.load("title.png", Texture.class);
		Conway.manager.load("textures.json", Skin.class); //TODO make the actual skin
		Conway.manager.finishLoading();

		//Instantiate title screen assets
		Conway.skin = Conway.manager.get("textures.json");
		title = new TextureRegion(new Texture(Gdx.files.internal("title.png")), 0, 0, 256, 256);
		instructions = new Label("Tap Anywhere to Continue", Conway.skin);
		instructions.setColor(1, 1, 1, 0);

		items.add(new Image(title)).padBottom(10).row();
		items.add(instructions);

		//Load the rest of the assets
	}

	@Override
	public void update(float delta) {
		if(Conway.manager.update()) {
			time += delta;
			instructions.setColor(1, 1, 1, Math.abs(MathUtils.cos(time)));

			if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
				game.setScreen(new Menu(game));
			}
		}
	}

	@Override
	public void hide() {
		batch.dispose();
		title.getTexture().dispose();
	}
}
