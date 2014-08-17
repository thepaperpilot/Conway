package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.Cell;
import com.thepaperpilot.Conway;
import com.thepaperpilot.ConwayButton;
import com.thepaperpilot.GameOfLife;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class Menu extends ConwayScreen {
	public static final ArrayList<GameOfLife> levels = getLevels();
	private GameOfLife background;

	static ArrayList<GameOfLife> getLevels() {
		ArrayList<GameOfLife> levels = new ArrayList<GameOfLife>();
		JSONParser parser = new JSONParser();
		JSONArray input = null;
		try {
			input = (JSONArray) ((JSONObject) parser.parse(Gdx.files.internal("levels.json").reader())).get("levels");
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ParseException e) {
			e.printStackTrace();
		}
		assert input != null;
		int index = 0;
		for(Object obj : input) {
			JSONObject jsonObject = (JSONObject) obj;
			ArrayList<Vector2> initialCells = new ArrayList<Vector2>();
			Vector2 size = new Vector2(getInt(jsonObject, "x"), getInt(jsonObject, "y"));
			for(Object t : (JSONArray) jsonObject.get("initial")) {
				initialCells.add(new Vector2(getInt((JSONObject) t, "x") + size.x / 2, getInt((JSONObject) t, "y") + size.y / 2));
			}
			if(getInt(jsonObject, "objective") == 0) {
				levels.add(new GameOfLife(size, initialCells, (Boolean) jsonObject.get("warping"), getInt(jsonObject, "clicks"), index));
				continue;
			}
			ArrayList<Vector2> targets = new ArrayList<Vector2>();
			for(Object t : (JSONArray) jsonObject.get("targets"))
				targets.add(new Vector2(getInt((JSONObject) t, "x") + size.x / 2, getInt((JSONObject) t, "y") + size.y / 2));
			levels.add(new GameOfLife(size, initialCells, (Boolean) jsonObject.get("warping"), getInt(jsonObject, "objective") == 1, targets, getInt(jsonObject, "clicks"), index));
			index++;
		}
		return levels;
	}

	private static int getInt(JSONObject jsonObject, String key) {
		return ((Number) jsonObject.get(key)).intValue();
	}

	@Override
	public void show() {
		super.show();
		Table levels = new Table();
		for(final GameOfLife GoL : Menu.levels) {
			Image level = GoL.getImage(true);
			Table outerLevel = new Table();
			outerLevel.add(level).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getWidth() / 6);
			outerLevel.setBackground(Conway.skin.getDrawable("buttonDown"));
			levels.add(outerLevel).pad(4);
			outerLevel.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					transition(new GameScreen(getLevels().get(Menu.levels.indexOf(GoL))));
				}
			});
		}

		final ScrollPane carousel = new ScrollPane(levels, Conway.skin);
		carousel.addListener(new ActorGestureListener() {
			@Override
			public void fling(InputEvent event, float velocityX, float velocityY, int button) {
				carousel.setVelocityX(carousel.getVelocityX() + velocityX);
			}

			@Override
			public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
				carousel.setScrollX(carousel.getScrollX() + deltaX);
			}
		});

		background = new GameOfLife(new Vector2(MathUtils.ceil(10 * Gdx.graphics.getWidth() / GameOfLife.cellSize), MathUtils.ceil(10 * Gdx.graphics.getHeight() / GameOfLife.cellSize)));
		Label title = new Label("Conway's Game of Life\nThe Game", Conway.skin, "large");
		title.setAlignment(Align.center);
		title.setColor(1, 0, 0, 1);
		ConwayButton creative = new ConwayButton("Creative") {
			@Override
			public void clicked() {
				transition(new GameScreen(creative()));
			}
		};

		items.add(title).padBottom(40).row();
		items.add(levels).width(Gdx.graphics.getWidth() - 100).padBottom(10).row();
		items.add(creative);

		final Button soundButton = new Button(Conway.skin, "transparent");
		soundButton.add(new Image(Conway.manager.get(Conway.sound ? "soundOn.png" : "soundOff.png", Texture.class)));
		soundButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Conway.sound = !Conway.sound;
				soundButton.clearChildren();
				soundButton.add(new Image(Conway.manager.get(Conway.sound ? "soundOn.png" : "soundOff.png", Texture.class)));
				if(Conway.sound) {
					Conway.bgm.play();
					Conway.manager.get("step.wav", Sound.class).play();
				} else
					Conway.bgm.stop();
			}
		});
		Table sound = new Table();
		sound.setFillParent(true);
		sound.bottom().right().add(soundButton).pad(10);
		stage.addActor(sound);

		for(int i = 0; i < 1000; i++) {
			Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
			cell.live = true;
		}
	}

	static GameOfLife creative() {
		GameOfLife GoL = new GameOfLife(new Vector2(40, 40));
		GoL.clicks = -1;
		return GoL;
	}

	@Override
	void update(float delta) {
		if(background.update(delta, true, false))
			for(int i = 0; i < 100; i++) {
				Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
				cell.live = true;
			}
	}

	@Override
	public void render(float delta) {
		update(delta);
		stage.act(delta);
		stage.getActors().insert(0, background.getImage(false));
		stage.draw();
		stage.getActors().removeIndex(0);
	}

	@Override
	public void hide() {
		super.hide();
		background.dispose();
	}
}
