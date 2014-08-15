package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thepaperpilot.Cell;
import com.thepaperpilot.Conway;
import com.thepaperpilot.GameOfLife;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class Menu extends ConwayScreen {
	private GameOfLife background;

	@Override
	public void show() {
		super.show();
		Table levels = new Table();
		for(final GameOfLife GoL : getLevels()) {
			TextureRegion texture = new TextureRegion(GoL.getTexture());
			texture.flip(false, true);
			Image level = new Image(texture);
			level.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					transition(new GameScreen(GoL));
				}
			});
			Stack stack = new Stack();
			Table bg = new Table();
			bg.setBackground(Conway.skin.getDrawable("buttonDown"));
			Table outerLevel = new Table();
			outerLevel.add(level).pad(10);
			stack.add(bg);
			stack.add(outerLevel);
			levels.add(stack).width(20 + Gdx.graphics.getWidth() / 6).height(20 + Gdx.graphics.getWidth() / 6).pad(5);
		}

		final ScrollPane carousel = new ScrollPane(levels, Conway.skin);
		carousel.addListener(new ActorGestureListener() {
			@Override
			public void fling (InputEvent event, float velocityX, float velocityY, int button) {
				carousel.setVelocityX(carousel.getVelocityX() + velocityX);
			}

			@Override
			public void pan (InputEvent event, float x, float y, float deltaX, float deltaY) {
				carousel.setScrollX(carousel.getScrollX() + deltaX);
			}
        });

		background = new GameOfLife(new Vector2(MathUtils.ceil(10 * Gdx.graphics.getWidth() / GameOfLife.cellSize), MathUtils.ceil(10 * Gdx.graphics.getHeight() / GameOfLife.cellSize)));
		Label title = new Label("Conway's Game of Life\nThe Game", Conway.skin, "large");
		title.setAlignment(Align.center);
		title.setColor(1, 0, 0, 1);
		TextButton random = new TextButton("Random\nGame", Conway.skin);
		random.pad(10);
		random.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				transition(new GameScreen(random()));
			}
		});
		TextButton creative = new TextButton("Creative", Conway.skin);
		creative.pad(10);
		creative.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				transition(new GameScreen(creative()));
			}
		});

		items.add(title).padBottom(40).row();
		items.add(levels).width(Gdx.graphics.getWidth() - 100).padBottom(10).row();
		items.add(random).padBottom(10).row();
		items.add(creative);

		stage.getActors().reverse();

		for(int i = 0; i < 1000; i++) {
			Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
			cell.live = true;
		}
	}

	GameOfLife random() {
		Vector2 size = new Vector2(MathUtils.random(15, 30), MathUtils.random(15, 30));
		int initial = MathUtils.random(100, 200);
		ArrayList<Vector2> initialCells = new ArrayList<Vector2>();
		ArrayList<Vector2> targets = new ArrayList<Vector2>();
		for(int i = 0; i < initial; i++)
			initialCells.add(new Vector2(MathUtils.random(size.x), MathUtils.random(size.y)));
		int objective = MathUtils.random(2);
		if(objective == 0)
			return new GameOfLife(size, initialCells, MathUtils.randomBoolean(), MathUtils.random(100));
		while(MathUtils.random(targets.size()) < 5)
			GameScreen.fillSquare(targets, new Vector2(MathUtils.random(size.x - 2), MathUtils.random(size.y - 2)), new Vector2(MathUtils.random(1, 2), MathUtils.random(1, 2)));
		return new GameOfLife(size, initialCells, MathUtils.randomBoolean(), objective == 1, targets, MathUtils.random(100));
	}

	GameOfLife creative() {
		GameOfLife GoL = new GameOfLife(new Vector2(100, 100));
		GoL.clicks = -1;
		return GoL;
	}

	@Override
	public void update(float delta) {
		if(background.update(delta, true, true))
			for(int i = 0; i < 100; i++) {
				Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
				cell.live = true;
			}
		background.draw(transition == null ? 1 : reverse ? 1f - transition.getTime() : transition.getTime());
	}

	@Override
	public void hide() {
		super.hide();
		background.dispose();
	}

	ArrayList<GameOfLife> getLevels() {
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
		for(Object obj : input) {
			JSONObject jsonObject = (JSONObject) obj;
			ArrayList<Vector2> initialCells = new ArrayList<Vector2>();
			Vector2 size = new Vector2(getInt(jsonObject, "x"), getInt(jsonObject, "y"));
			for(Object t : (JSONArray) jsonObject.get("initial")) {
				initialCells.add(new Vector2(getInt((JSONObject) t, "x") + size.x / 2, getInt((JSONObject) t, "y") + size.y / 2));
			}
			if(getInt(jsonObject, "objective") == 0) {
				levels.add(new GameOfLife(size, initialCells, (Boolean) jsonObject.get("warping"), getInt(jsonObject, "clicks")));
				continue;
			}
			ArrayList<Vector2> targets = new ArrayList<Vector2>();
			for(Object t : (JSONArray) jsonObject.get("targets"))
				targets.add(new Vector2(getInt((JSONObject) t, "x") + size.x / 2, getInt((JSONObject) t, "y") + size.y / 2));
			levels.add(new GameOfLife(size, initialCells, (Boolean) jsonObject.get("warping"), getInt(jsonObject, "objective") == 1, targets, getInt(jsonObject, "clicks")));
		}
		return levels;
	}

	int getInt(JSONObject jsonObject, String key) {
		return ((Number) jsonObject.get(key)).intValue();
	}
}
