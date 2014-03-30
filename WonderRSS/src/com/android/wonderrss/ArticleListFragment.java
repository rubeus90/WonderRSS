package com.android.wonderrss;

import java.util.Map;

import com.android.wonderrss.Task.RssService;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

public class ArticleListFragment extends ListFragment {
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
			throw new ClassCastException(activity.toString()
					+ " must implement OnListItemClickListener");
		}
	}

	public ArticleListFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.article_list_fragment, container,
				false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Retain the instance of the fragment
		setRetainInstance(true);

		//Load the content of the stream
		fetchFeed();

		//Initialise the keyboard
		keyboard = (InputMethodManager) getActivity().getSystemService(ListActivity.INPUT_METHOD_SERVICE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		listener.onItemClick(position);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu items for use in the action bar
		inflater.inflate(R.menu.default_menu, menu);

		addItem = menu.findItem(R.id.action_new);
		editURL = (EditText) addItem.getActionView();

		//Listener for the ActionView
		editURL.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//If the user pressed the Enter key, we reload the stream, collapse the ActionView and clear the text field
				if (keyCode == 66) {
					saveUrl(getActivity(), editURL.getText().toString());
					fetchFeed();
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
		case R.id.action_remove:
			deleteUrl(getActivity());
			fetchFeed();
			return true;
		case R.id.action_refresh:
			fetchFeed();
			return true;
		case R.id.action_new:
			keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //toggle the keyboard
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//Execute the AsyncTask to load the stream
	@SuppressWarnings("unchecked")
	public void fetchFeed() {
		try{
			if(!isConnectedToInternet())
				Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
			else{
				rss = new RssService(this);
				rss.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loadUrl(getActivity())); //possibility to execute AsyncTasks in parallel
			}
		}
		catch(Exception e){
			e.printStackTrace();			
		}
	}

	//Check if the device is connected to the internet
	public boolean isConnectedToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getActivity()
				.getSystemService(ListActivity.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	//Save the URL to SharedPreferences
	public void saveUrl(Context context, String url) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(url, url);
		edit.commit();
	}

	//Retrieve the URL from SharedPreferences
	public Map<String, ?> loadUrl(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Map<String, ?> map = prefs.getAll();
		return map;
	}
	
	//Clear all the URLs saved in SharedPreferences
	public void deleteUrl(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	//We dismiss the ProgressBar when the fragment is detached from the activity (eg: on orientation change, the activity is destroyed)
	@Override
	public void onDetach() {
		super.onDetach();
		rss.getProgress().dismiss();
	}
	
}
