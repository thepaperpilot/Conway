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
	public static final ArrayList<ArrayList<GameOfLife>> levels = getLevels();
	public static int tab = 0;
	private GameOfLife background;

	private static ArrayList<ArrayList<GameOfLife>> getLevels() {
		ArrayList<ArrayList<GameOfLife>> levels = new ArrayList<ArrayList<GameOfLife>>();
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
		for(int i = 0; i < input.size(); i++) {
			levels.add(new ArrayList<GameOfLife>());
			for(int i2 = 0; i2 < ((JSONArray) input.get(i)).size(); i2++) {
				JSONObject jsonObject = (JSONObject) ((JSONArray) input.get(i)).get(i2);
				ArrayList<Vector2> initialCells = new ArrayList<Vector2>();
				Vector2 size = new Vector2(getInt(jsonObject, "x"), getInt(jsonObject, "y"));
				for(Object t : (JSONArray) jsonObject.get("initial")) {
					initialCells.add(new Vector2(getInt((JSONObject) t, "x") + size.x / 2, getInt((JSONObject) t, "y") + size.y / 2));
				}
				if(getInt(jsonObject, "objective") == 0) {
					levels.get(i).add(new GameOfLife(size, initialCells, (Boolean) jsonObject.get("warping"), getInt(jsonObject, "clicks"), i2));
					continue;
				}
				ArrayList<Vector2> targets = new ArrayList<Vector2>();
				for(Object t : (JSONArray) jsonObject.get("targets"))
					targets.add(new Vector2(getInt((JSONObject) t, "x") + size.x / 2, getInt((JSONObject) t, "y") + size.y / 2));
				levels.get(i).add(new GameOfLife(size, initialCells, (Boolean) jsonObject.get("warping"), getInt(jsonObject, "objective") == 1, targets, getInt(jsonObject, "clicks"), i2));
			}
		}
		return setChecked(levels);
	}

	private static ArrayList<ArrayList<GameOfLife>> setChecked(ArrayList<ArrayList<GameOfLife>> levels) {
		JSONParser parser = new JSONParser();
		JSONObject input = null;
		if(!Gdx.files.local("data.json").exists())
			createData();
		try {
			input = (JSONObject) parser.parse(Gdx.files.local("data.json").reader());
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ParseException e) {
			e.printStackTrace();
		}
		assert input != null;
		JSONArray checks = (JSONArray) input.get("levels");
		if(checks != null)
			for(Object obj : checks) {
				JSONObject jsonObject = (JSONObject) obj;
				levels.get(getInt(jsonObject, "tab")).get(getInt(jsonObject, "n")).completed = true;
			}
		Conway.sound = (Boolean) input.get("sound");
		if(Conway.sound) Conway.bgm.play();
		return levels;
	}

	@SuppressWarnings("unchecked")
	private static void createData() {
		JSONObject data = new JSONObject();
		data.put("sound", Conway.sound);
		JSONArray checks = new JSONArray();
		data.put("levels", checks);
		Gdx.files.local("data.json").writeString(data.toJSONString(), false);
	}

	@SuppressWarnings("unchecked")
	public static void writeData() {
		JSONObject data = new JSONObject();
		data.put("sound", Conway.sound);
		JSONArray checks = new JSONArray();
		for(int i = 0; i < levels.size(); i++) {
			for(int i2 = 0; i2 < levels.get(i).size(); i2++) {
				if(levels.get(i).get(i2).completed) {
					JSONObject JSONlevel = new JSONObject();
					JSONlevel.put("tab", i);
					JSONlevel.put("n", i2);
					checks.add(JSONlevel);
				}
			}
		}
		data.put("levels", checks);
		Gdx.files.local("data.json").writeString(data.toJSONString(), false);
	}

	private static int getInt(JSONObject jsonObject, String key) {
		return ((Number) jsonObject.get(key)).intValue();
	}

	static GameOfLife creative() {
		GameOfLife GoL = new GameOfLife(new Vector2(40, 40));
		GoL.clicks = -1;
		return GoL;
	}

	Table levelSelector() {
		Table levels = new Table();
		for(final GameOfLife GoL : Menu.levels.get(Menu.tab)) {
			Image level = GoL.getImage(true);
			Button outerLevel = new Button(Conway.skin);
			outerLevel.add(level);
			if(GoL.completed) {
				Table check = new Table();
				check.top().right().add(new Image(Conway.manager.get("check.png", Texture.class)));
				Stack outerOuterLevel = new Stack();
				outerOuterLevel.add(outerLevel);
				outerOuterLevel.add(check);
				levels.add(outerOuterLevel).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getWidth() / 6).pad(4);
			} else levels.add(outerLevel).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getWidth() / 6).pad(4);
			outerLevel.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if(Conway.sound)
						Conway.manager.get("step.wav", Sound.class).play();
					transition(new GameScreen(GoL.copy()));
				}
			});
		}
		return levels;
	}

	public void createStage() {
		Table tabs = new Table();
		ConwayButton tuts = new ConwayButton("Tutorial") {
			@Override
			public void clicked() {
				tab = 0;
				createStage();
			}
		};
		ConwayButton easy = new ConwayButton("Easy") {
			@Override
			public void clicked() {
				tab = 1;
				createStage();
			}
		};
		ConwayButton medium = new ConwayButton("Medium") {
			@Override
			public void clicked() {
				tab = 2;
				createStage();
			}
		};
		ConwayButton hard = new ConwayButton("Hard") {
			@Override
			public void clicked() {
				tab = 3;
				createStage();
			}
		};
		tabs.add(tuts).pad(5);
		tabs.add(easy).pad(5);
		tabs.add(medium).pad(5);
		tabs.add(hard).pad(5);

		final ScrollPane carousel = new ScrollPane(levelSelector(), Conway.skin);
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

		Label title = new Label("Conway", Conway.skin, "large");
		title.setAlignment(Align.center);
		title.setFontScale(5);
		title.setColor(.7f, .7f, .7f, 1);
		ConwayButton creative = new ConwayButton("Creative") {
			@Override
			public void clicked() {
				transition(new GameScreen(creative()));
			}
		};

		stage.getActors().clear();
		items = new Table();
		items.setFillParent(true);
		items.add(title).padBottom(40).row();
		items.add(tabs).row();
		items.add(carousel).width(Gdx.graphics.getWidth() - 100).padBottom(30).row();
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
					Conway.bgm.pause();
				writeData();
			}
		});
		Table sound = new Table();
		sound.setFillParent(true);
		sound.bottom().right().add(soundButton).pad(10);

		stage.addActor(items);
		stage.addActor(sound);
	}

	@Override
	public void show() {
		super.show();

		createStage();

		background = new GameOfLife(new Vector2(MathUtils.ceil(10 * Gdx.graphics.getWidth() / GameOfLife.cellSize), MathUtils.ceil(10 * Gdx.graphics.getHeight() / GameOfLife.cellSize)));
		for(int i = 0; i < 1000; i++) {
			Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
			cell.live = true;
		}
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
