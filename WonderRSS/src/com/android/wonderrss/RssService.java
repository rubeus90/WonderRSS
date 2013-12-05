package com.android.wonderrss;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class RssService	extends AsyncTask<String, Void, Feed> {
	
	private Activity activity;
	private ArticleListFragment fragment;
	private RssParser theRSSHandler;
	private ListAdapter adapter;
	private ProgressDialog progress;
	static Feed stream;

	public RssService(ArticleListFragment fragment) {	
		this.fragment = fragment;
		activity = fragment.getActivity();
	}

	public void onPreExecute() {
		progress = new ProgressDialog(activity);
		progress.setMessage(activity.getResources().getString(R.string.downloading));
		progress.show();
	}

	public void onPostExecute(final Feed feed) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				List<FeedArticle> list = feed.getListe();					
				HashMap<String, String> map;
				List<HashMap<String, String>> listMap = new ArrayList<HashMap<String, String>>();
				for (FeedArticle article : list) {
					map = new HashMap<String, String>();
					map.put("title", article.getTitle());
					map.put("date", article.getPubDate() + " by " + article.getAuthor());
					listMap.add(map);
				}
//				adapter = new SimpleAdapter(activity, listMap,
//						android.R.layout.simple_list_item_2, new String[] {
//								"title", "description" }, new int[] {
//								android.R.id.text1, android.R.id.text2 });
				adapter = new CustomListAdapter(activity, listMap);
				fragment.setListAdapter(adapter);	
			}
		});
		
		progress.dismiss();
	}

	@Override
	protected Feed doInBackground(String... arg0) {
		
		try {
            URL url= new URL(arg0[0]);
            
            SAXParserFactory factory =SAXParserFactory.newInstance();
            SAXParser parser=factory.newSAXParser();
            XMLReader xmlreader=parser.getXMLReader();
            
            theRSSHandler=new RssParser();
            xmlreader.setContentHandler(theRSSHandler);
            InputSource is=new InputSource(url.openStream());
            
            xmlreader.parse(is);
            stream = theRSSHandler.getFeed();
            return stream;
        } catch (Exception e) {
        	e.printStackTrace();
        	stream = new Feed();
            return stream;
        }
	}
	
	public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      } 
          }
          return false;
    }
}
