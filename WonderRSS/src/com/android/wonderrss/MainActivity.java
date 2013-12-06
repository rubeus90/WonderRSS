package com.android.wonderrss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity implements ArticleListFragment.OnListItemClickListener{
	public static FeedDbManager manager;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  
        manager = new FeedDbManager(this);
    }

	@Override
	public void onItemClick(int position) {
		ArticleDetailFragment detailFragment = (ArticleDetailFragment) getFragmentManager().findFragmentById(R.id.detailfragment);
		
		//if the fragment is not in the layout
		if (detailFragment == null) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        } 
		//if the fragment is already in the layout
		else {
            detailFragment.updateContent(position);
        }
	}
}
