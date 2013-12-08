package com.android.wonderrss;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

public class ArticleListFragment extends ListFragment{
	private String url;
	private RssService rss;
	private EditText editURL;
	private MenuItem addItem;
	private InputMethodManager keyboard;
	private OnListItemClickListener listener;
	
	public interface OnListItemClickListener {
        public void onItemClick(int position);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (OnListItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnListItemClickListener");
        }
    }
	
	public ArticleListFragment() {
    	setHasOptionsMenu(true);	
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.article_list_fragment, container, false);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			url = savedInstanceState.getString("url");
		}

		fetchFeed();

		keyboard = (InputMethodManager) getActivity().getSystemService(MainActivity.INPUT_METHOD_SERVICE);
	}
	
	@Override
	public void onListItemClick (ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		
		listener.onItemClick(position);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu items for use in the action bar
//		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.default_menu, menu);

		addItem = menu.findItem(R.id.action_new);
		editURL = (EditText) addItem.getActionView();

		editURL.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				url = editURL.getText().toString();
				rss = new RssService(ArticleListFragment.this);
				if (keyCode == 66) {
					rss.execute(url);
					addItem.collapseActionView();
					editURL.setActivated(false);
					editURL.setText("");
				}
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_refresh:
			fetchFeed();
			return true;
		case R.id.action_new:
			keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	// url = savedInstanceState.getString("url");
	// }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("url", url);
	}

	public void fetchFeed() {
		rss = new RssService(this);
		rss.execute(url);			
	}
	
	public void setUrl(String url){
		this.url = url;
	}
}
