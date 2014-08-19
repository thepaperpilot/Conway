package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.thepaperpilot.Conway;

public class Title extends ConwayScreen {
	private Label instructions;
	private float time = 0;

	@Override
	public void show() {
		super.show();
		//Load title screen assets
		Conway.manager.load("libgdx.png", Texture.class);
		Conway.manager.load("textures.json", Skin.class); //TODO make the actual skin
		Conway.manager.finishLoading();

		//Instantiate title screen assets
		Conway.skin = Conway.manager.get("textures.json");
		Conway.skin.getFont("font").setScale(Gdx.graphics.getWidth() / 500f);
		Conway.skin.getFont("large").setScale(Gdx.graphics.getWidth() / 500f);
		TextureRegion title = new TextureRegion(new Texture(Gdx.files.internal("libgdx.png")), 0, 0, 240, 76);
		instructions = new Label("Tap Anywhere to Continue", Conway.skin);
		instructions.setColor(1, 1, 1, 0);

		items.add(new Image(title)).padBottom(10).row();
		items.add(instructions);

		//Load the rest of the assets
		Conway.manager.load("states.atlas", TextureAtlas.class);
		Conway.manager.load("soundOn.png", Texture.class);
		Conway.manager.load("soundOff.png", Texture.class);
		Conway.manager.load("check.png", Texture.class);
		Conway.manager.load("step.wav", Sound.class);
		Conway.manager.load("bgm.ogg", Music.class);
	}

	@Override
	public void update(float delta) {
		if(Conway.manager.update()) {
			time += delta;
			instructions.setColor(1, 1, 1, Math.abs(MathUtils.sin(time)));


			if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
				Conway.bgm = Conway.manager.get("bgm.ogg", Music.class);
				Conway.bgm.setVolume(.5f);
				Conway.bgm.setLooping(true);
				transition(new Menu());
			}
		}
	}
}
