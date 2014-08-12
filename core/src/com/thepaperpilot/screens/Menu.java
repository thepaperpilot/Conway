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

import java.util.ArrayList;

public class Menu extends ConwayScreen {
	String objective;
	GameOfLife background;
	Label title;
	TextButton random;
	TextButton creative;

	public Menu() {
		super();
		background = new GameOfLife(new Vector2(MathUtils.ceil(10 * Gdx.graphics.getWidth() / GameOfLife.cellSize), MathUtils.ceil(10 * Gdx.graphics.getHeight() / GameOfLife.cellSize)), new ArrayList<Vector2>(), new ArrayList<Vector2>(), true);
		title = new Label("Conway's Game of Life\nThe Game", Conway.skin, "large");
		title.setAlignment(Align.center);
		title.setColor(1, 0, 0, 1);
		random = new TextButton("Random\nGame", Conway.skin, "button");
		random.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Conway.getGame().setScreen(new GameScreen(random(), objective));
			}
		});
		creative = new TextButton("Creative", Conway.skin, "button");
		creative.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Conway.getGame().setScreen(new GameScreen(creative(), objective));
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

	public GameOfLife random() {
		Vector2 size = new Vector2(MathUtils.random(15, 30), MathUtils.random(15, 30));
		int initial = MathUtils.random(100, 200);
		ArrayList<Vector2> initialCells = new ArrayList<Vector2>();
		ArrayList<Vector2> targets = new ArrayList<Vector2>();
		for(int i = 0; i < initial; i++)
			initialCells.add(new Vector2(MathUtils.random(size.x), MathUtils.random(size.y)));
		while(MathUtils.random(targets.size()) < 5)
			GameScreen.fillSquare(targets, new Vector2(MathUtils.random(size.x - 2), MathUtils.random(size.y - 2)), new Vector2(MathUtils.random(1, 2), MathUtils.random(1, 2)));
		switch(MathUtils.random(3)) {
			default:
				objective = "Kill the entire population";
				return new GameOfLife(size, new ArrayList<Vector2>(), initialCells, MathUtils.randomBoolean()) {
					@Override
					public boolean checkCompletion() {
						for(Cell[] row : grid)
							for(Cell cell : row)
								if(cell.live)
									return false;
						return  true;
					}
				};
			case 1:
				final int target = MathUtils.random(50, 100);
				objective = "Reach a population of " + target;
				return new GameOfLife(size, new ArrayList<Vector2>(), initialCells, MathUtils.randomBoolean()) {
					@Override
					public boolean checkCompletion() {
						int pop = 0;
						for(Vector2 pos : targets)
							if(grid[(int) pos.x][(int) pos.y].live)
								pop++;
						return pop >= target;
					}
				};
			case 2:
				objective = "Kill all targets";
				return new GameOfLife(size, targets, initialCells, MathUtils.randomBoolean()) {
					@Override
					public boolean checkCompletion() {
						for(Vector2 pos : targets)
							if(grid[(int) pos.x][(int) pos.y].live)
								return false;
						return true;
					}
				};
			case 3:
				objective = "Populate all targets";
				return new GameOfLife(size, targets, initialCells, MathUtils.randomBoolean()) {
					@Override
					public boolean checkCompletion() {
						for(Vector2 pos : targets)
							if(!grid[(int) pos.x][(int) pos.y].live)
								return false;
						return true;
					}
				};
		}
	}

	public GameOfLife creative() {
		return new GameOfLife(new Vector2(100, 100), new ArrayList<Vector2>(), new ArrayList<Vector2>(), true);
	}

	@Override
	public void update(float delta) {
		background.update(delta, true, true);
		for(int i = 0; i < 10; i++) {
			Cell cell = background.grid[MathUtils.random(background.grid.length - 1)][MathUtils.random(background.grid[0].length - 1)];
			cell.live = true;
		}
		background.draw();
	}

	@Override
	public void hide() {
		batch.dispose();
		background.dispose();
	}
}
