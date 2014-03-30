package com.android.wonderrss.Parallax;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class ParallaxListView extends ListView implements OnScrollListener{
	private View parallaxedView;

	public ParallaxListView(Context context) {
		super(context);
	}
	
	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		parallax();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
	public void parallax(){
		if (parallaxedView != null) {
			if (getChildCount() > 0) {
				int top = -getChildAt(0).getTop();
				float factor = 1.9f;
				parallaxedView = getChildAt(0);
				parallaxedView.setTranslationY((float)top / factor);
			}
		}
	}
	
	public void addParallaxHeaderView(View view){
		parallaxedView = view;
		super.addHeaderView(parallaxedView, null, false);
//		addParallaxedView(view);
		this.setOnScrollListener(this);
	}
	
//	public void addParallaxedView(View view){
//		parallaxedView = new ParallaxedView(view);
//	}
}
