package com.android.wonderrss;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements ArticleListFragment.OnListItemClickListener{
	public static FeedDbManager manager;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);
        
        Intent intent = getIntent();
        String action = intent.getDataString();
        
        ArticleListFragment listFragment = (ArticleListFragment) getFragmentManager().findFragmentById(R.id.listfragment);
        listFragment.setUrl(action);
        listFragment.fetchFeed();
        
        manager = new FeedDbManager(this);
    }

	@Override
	public void onItemClick(int position) {
		ArticleDetailFragment detailFragment = (ArticleDetailFragment) getFragmentManager().findFragmentById(R.id.detailfragment);
		
		//if the fragment is not in the layout
//		if (detailFragment == null) {
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			Log.v("Orientation", "Je suis en mode portrait");
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        } 
		//if the fragment is already in the layout
		else {
			Log.v("Orientation", "Je suis en mode paysage");
            detailFragment.updateContent(position);
        }
	}
}
