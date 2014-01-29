package com.android.wonderrss;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class ListActivity extends Activity implements ArticleListFragment.OnListItemClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		//If the application is launched from the browser (via an intent), we retrieve the URL
		Intent intent = getIntent();
		String action = intent.getDataString();

		ArticleListFragment listFragment = (ArticleListFragment) getFragmentManager().findFragmentById(R.id.listfragment);
		
		//If it was indeed launched from the browser, we save the URL and we refresh the stream
		if (action != null) {
			listFragment.saveUrl(this, action);
			listFragment.fetchFeed();
		}
	}

	@Override
	public void onItemClick(int position) {
		ArticleDetailFragment detailFragment = (ArticleDetailFragment) getFragmentManager().findFragmentById(R.id.detailfragment);

		//If the orientation is portrait (so the 2nd fragment is not in the layout
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("Orientation", "Je suis en mode portrait");
			Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtra("position", position);
			startActivity(intent);
		}
		//If the fragment is already in the layout (in landscape mode)
		else {
			Log.i("Orientation", "Je suis en mode paysage");
			detailFragment.updateContent(position);
		}
	}
}
