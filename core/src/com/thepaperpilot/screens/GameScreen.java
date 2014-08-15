package com.thepaperpilot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.thepaperpilot.Cell;
import com.thepaperpilot.Conway;
import com.thepaperpilot.GameOfLife;
import java.util.ArrayList;

public class GameScreen extends ConwayScreen implements GestureDetector.GestureListener {
	private final GameOfLife game;
	private boolean stepping = false;
	private boolean fast = false;
	private TextButton toggleStepping;
	private TextButton stepFastForward;
    private TextButton resetLevel;
	private boolean won = false;
	private Label clicksLabel;

	public GameScreen(final GameOfLife game) {
		this.game = game;
	}

	public static void fillSquare(ArrayList<Vector2> cells, Vector2 pos, Vector2 size) {
		for(int i = (int) pos.x; i < pos.x + size.x; i++) {
			for(int i2 = (int) pos.y; i2 < pos.y + size.y; i2++) {
				cells.add(new Vector2(i, i2));
			}
		}
	}

	@Override
	public void show() {
		super.show();
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(new GestureDetector(this));
		toggleStepping = new TextButton("Go", Conway.skin);
		stepFastForward = new TextButton("Step", Conway.skin);
        resetLevel = new TextButton("Reset", Conway.skin);
		toggleStepping.pad(Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f, Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f);
		stepFastForward.pad(Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f, Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f);
        resetLevel.pad(Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f, Gdx.graphics.getHeight() / 100f, Gdx.graphics.getWidth() / 100f);
		toggleStepping.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!won) {
					stepping = !stepping;
					toggleStepping.setText(stepping ? "Stop" : "Go");
					stepFastForward.setText(stepping ? fast ? "Slow" : "Fast" : "Step");
				}
			}
		});
		stepFastForward.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!won) {
					if(stepping) {
						fast = !fast;
						stepFastForward.setText(fast ? "Slow" : "Fast");
					} else {
						game.step();
						if(game.objective != null && game.objective.checkCompletion()) win();
					}
				}
			}
		});
        resetLevel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });
		if(game.objective != null) {
			Table objectiveTable = new Table();
			objectiveTable.setFillParent(true);
			Table innerObjectiveTable = new Table();
			innerObjectiveTable.setBackground(Conway.skin.get("buttonUp", Drawable.class));
			innerObjectiveTable.pad(5, 10, 5, 10);
			Label objectiveLabel = new Label(game.objective.objective, Conway.skin);
			innerObjectiveTable.add(objectiveLabel);
			objectiveTable.top().add(innerObjectiveTable);
			stage.addActor(objectiveTable);
		}
		if(game.clicks > 0) {
			Table clicksTable = new Table();
			clicksTable.setFillParent(true);
			Table innerClicksTable = new Table();
			innerClicksTable.setBackground(Conway.skin.get("buttonUp", Drawable.class));
			innerClicksTable.pad(5, 10, 5, 10);
			clicksLabel = new Label(String.valueOf(game.clicks), Conway.skin);
			innerClicksTable.add(clicksLabel);
			clicksTable.top().right().add(innerClicksTable);
			stage.addActor(clicksTable);
		}
		items.bottom().left();
		items.add(toggleStepping).pad(2);
		items.add(stepFastForward).pad(2);
        items.add(resetLevel).pad(2);
	}

	@Override
	public void pause() {
		stepping = false;
		toggleStepping.setText("Go");
		stepFastForward.setText("Step");
	}

	@Override
	public void render(float delta) {
		update(delta);
		stage.act(delta);
		game.draw(transition == null ? 1 : reverse ? 1f - transition.getTime() : transition.getTime());
		stage.draw();
	}

	void update(float delta) {
		if(game.update(delta, stepping, fast) && game.objective != null && game.objective.checkCompletion()) win();
	}

	private void win() {
		if(!won) {
			won = true;
			stepping = false;
			Table victory = new Table();
			victory.setTransform(true);
			victory.setCenterPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 2 / 3);
			victory.setColor(1, 1, 1, 0);
			victory.setScale(.5f);
			final Table buttons = new Table();
			buttons.setCenterPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 3);
			TextButton menu = new TextButton("Back to Menu", Conway.skin);
			menu.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					transition(new Menu());
				}
			});
			menu.pad(10);
			TextButton next = new TextButton("Next Level", Conway.skin);
			next.pad(10);
			buttons.add(menu).pad(10).row();
			buttons.add(next).pad(10);
			buttons.setColor(1, 1, 1, 0);
			victory.addAction(Actions.sequence(Actions.parallel(Actions.scaleBy(2, 2, 2, Interpolation.elastic), Actions.fadeIn(2)), Actions.run(new Runnable() {
				@Override
				public void run() {
					stage.addActor(buttons);
					buttons.addAction(Actions.fadeIn(2));
				}
			})));
			Label you = new Label("You", Conway.skin, "large");
			Label win = new Label("Win!", Conway.skin, "large");
			you.addAction(Actions.moveBy(-Gdx.graphics.getWidth() / 9, 0, 4, Interpolation.bounce));
			win.addAction(Actions.moveBy(Gdx.graphics.getWidth() / 9, 0, 4, Interpolation.bounce));
			victory.add(you).row();
			victory.add(win);
			stage.addActor(victory);
		}
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if(game.getBounds().contains(x, Gdx.graphics.getHeight() - y) && (game.clicks > 0 || game.clicks == -1)) {
			for(Cell[] row : game.grid) {
				for(Cell cell : row) {
					if(game.getCellBounds(cell).contains(x, Gdx.graphics.getHeight() - y)) {
						game.toggle(cell);
						if(game.clicks != -1) {
							game.clicks--;
							clicksLabel.setText(String.valueOf(game.clicks));
						}
						return true;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		game.pan.x = MathUtils.clamp(game.pan.x + (deltaX / game.zoom), -100 - (game.size.x * GameOfLife.cellSize) / 2, 100 + (game.size.x * GameOfLife.cellSize) / 2);
		game.pan.y = MathUtils.clamp(game.pan.y - (deltaY / game.zoom), -100 - (game.size.y * GameOfLife.cellSize) / 2, 100 + (game.size.y * GameOfLife.cellSize) / 2);
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		game.zoom = MathUtils.clamp(game.zoom + (distance - initialDistance) / 100000f, .1f, 1);
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void hide() {
		super.hide();
		game.dispose();
	}
}