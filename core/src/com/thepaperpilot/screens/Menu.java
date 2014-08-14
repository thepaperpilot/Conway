package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
		background = new GameOfLife(new Vector2(MathUtils.ceil(10 * Gdx.graphics.getWidth() / GameOfLife.cellSize), MathUtils.ceil(10 * Gdx.graphics.getHeight() / GameOfLife.cellSize)));
		Label title = new Label("Conway's Game of Life\nThe Game", Conway.skin, "large");
		title.setAlignment(Align.center);
		title.setColor(1, 0, 0, 1);
		TextButton random = new TextButton("Random\nGame", Conway.skin);
		random.pad(Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f, Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f);
		random.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				transition(new GameScreen(random()));
			}
		});
		TextButton creative = new TextButton("Creative", Conway.skin);
		creative.pad(Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f, Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f);
		creative.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				transition(new GameScreen(creative()));
			}
		});

		items.add(title).padBottom(40).row();
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
			return new GameOfLife(size, initialCells, MathUtils.randomBoolean());
		while(MathUtils.random(targets.size()) < 5)
			GameScreen.fillSquare(targets, new Vector2(MathUtils.random(size.x - 2), MathUtils.random(size.y - 2)), new Vector2(MathUtils.random(1, 2), MathUtils.random(1, 2)));
		return new GameOfLife(size, initialCells, MathUtils.randomBoolean(), objective == 1, targets);
	}

	GameOfLife creative() {
		return new GameOfLife(new Vector2(100, 100));
	}

	@Override
	public void update(float delta) {
		background.update(delta, true, true);
		for(int i = 0; i < 10; i++) {
			Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
			cell.live = true;
		}
		background.draw(transition == null ? 1 : reverse ? 1f - transition.getTime() : transition.getTime());
	}

	@Override
	public void dispose() {
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
			for(Object t : (JSONArray) jsonObject.get("initial"))
				initialCells.add(new Vector2(getInt((JSONObject) t, "x"), getInt((JSONObject) t, "y")));
			if(getInt(jsonObject, "objective") == 0) {
				levels.add(new GameOfLife(new Vector2(getInt(jsonObject, "x"), getInt(jsonObject, "y")), initialCells, (Boolean) jsonObject.get("warping")));
				continue;
			}
			ArrayList<Vector2> targets = new ArrayList<Vector2>();
			for(Object t : (JSONArray) jsonObject.get("targets"))
				targets.add(new Vector2(getInt((JSONObject) t, "x"), getInt((JSONObject) t, "y")));
			levels.add(new GameOfLife(new Vector2(getInt(jsonObject, "x"), getInt(jsonObject, "y")), initialCells, (Boolean) jsonObject.get("warping"), getInt(jsonObject, "objective") == 1, targets));
		}
		return levels;
	}

	int getInt(JSONObject jsonObject, String key) {
		return ((Number) jsonObject.get(key)).intValue();
	}
}
