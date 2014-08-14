package com.thepaperpilot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.thepaperpilot.screens.GameScreen;
import com.thepaperpilot.screens.Menu;

public class Input implements InputProcessor {
	public static Input instance = new Input();

	public Input() {
		Gdx.input.setCatchBackKey(true);
	}

	public static Input getInstance() {
		return instance;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK) {
			if(Conway.getGame().getScreen() instanceof GameScreen) {
				Conway.getGame().setScreen(new Menu());
			} else if(Conway.getGame().getScreen() instanceof Menu) {
				Gdx.app.exit();
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
