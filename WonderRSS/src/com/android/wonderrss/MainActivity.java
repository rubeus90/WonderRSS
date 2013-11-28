package com.android.wonderrss;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity{
	private String url;
	private ListView listView;
	private RssService rss;
	private EditText editURL;
	private MenuItem addItem;
	InputMethodManager keyboard;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        
        listView = (ListView) findViewById(R.id.listView); 
        
        if(savedInstanceState != null){
        	url = savedInstanceState.getString("url");
        }
        
        fetchFeed();     
        
        keyboard = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.default_menu, menu);
	    
	    addItem = menu.findItem(R.id.action_new);
	    editURL = (EditText) addItem.getActionView();
	    
	    editURL.setOnKeyListener(new View.OnKeyListener() {
			
			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				url = editURL.getText().toString();
				rss = new RssService(MainActivity.this, listView);
				if(keyCode == 66){
					rss.execute(url);
					addItem.collapseActionView();
					editURL.setActivated(false);
					keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				}
				return true;
			}
		});
	    
	    MenuItemCompat.setOnActionExpandListener(addItem, new MenuItemCompat.OnActionExpandListener() {
	        @Override
	        public boolean onMenuItemActionCollapse(MenuItem item) {
	            return true;  // Return true to collapse action view
	        }

	        @Override
	        public boolean onMenuItemActionExpand(MenuItem item) {
	        	editURL.requestFocus();
	            return true;  // Return true to expand action view
	        }
	    });
	    
	    return super.onCreateOptionsMenu(menu);
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
	
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//	    super.onRestoreInstanceState(savedInstanceState);
//	    url = savedInstanceState.getString("url");
//	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("url", url);	    
	}
	
	public void fetchFeed(){
		if(url == null){
        	List<String> message = new ArrayList<String>();
    		message.add(getResources().getString(R.string.no_rss));
    		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, message);
    		listView.setAdapter(adapter);
        }
        else{
          rss = new RssService(this, listView);
          rss.execute(url);
        }      
	}
}
