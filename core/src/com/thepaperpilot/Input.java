package com.thepaperpilot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class Input implements InputProcessor {
	private static Input instance = new Input();

	public static Input getInstance() {
		return instance;
	}

	private Input() {
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK){
			/* TODO Transition back to menu
			if(Conway.getGame().getScreen() instanceof GameScreen) {

			}
			*/
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
