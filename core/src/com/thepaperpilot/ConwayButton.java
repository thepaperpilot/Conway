package com.thepaperpilot;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConwayButton extends TextButton {
	public ConwayButton(String text) {
		super(text, Conway.skin);
		pad(10).padTop(20).padBottom(20);
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(Conway.sound)
					Conway.manager.get("step.wav", Sound.class).play();
				ConwayButton.this.clicked();
			}
		});
	}

	public void clicked() {

	}
}
