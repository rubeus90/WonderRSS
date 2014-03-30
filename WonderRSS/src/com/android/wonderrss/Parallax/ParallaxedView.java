package com.android.wonderrss.Parallax;

import android.view.View;

public class ParallaxedView {
	private View view;
	
	public ParallaxedView(View view){
		this.view = view;
	}
	
	public void move(float offset){
		view.setTranslationY(offset);
	}
}
