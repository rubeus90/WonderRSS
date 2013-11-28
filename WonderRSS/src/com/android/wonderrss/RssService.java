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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RssService	extends AsyncTask<String, Void, Feed> {
	
	private Activity activity;
	private ListView listView;
	private RssParser theRSSHandler;
	private ListAdapter adapter;
	private ProgressDialog progress;

	public RssService(Activity activity, ListView listView) {
		this.activity = activity;
		this.listView = listView;		
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
				if(feed != null){
					List<FeedArticle> list = feed.getListe();
					HashMap<String, Spanned> map;
					List<HashMap<String, Spanned>> listMap = new ArrayList<HashMap<String, Spanned>>();
					for (FeedArticle article : list) {
						map = new HashMap<String, Spanned>();
						map.put("title", Html.fromHtml(article.getTitle()));
						map.put("description", Html.fromHtml(article.getDescription()));
						listMap.add(map);
					}
					adapter = new SimpleAdapter(activity, listMap,
							android.R.layout.simple_list_item_2, new String[] {
									"title", "description" }, new int[] {
									android.R.id.text1, android.R.id.text2 });
					listView.setAdapter(adapter);
				}		
				else{
					List<String> message = new ArrayList<String>();
					if(!isConnectedToInternet()){
						message.add(activity.getResources().getString(R.string.wrong_url));
						adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, message);
						listView.setAdapter(adapter);
					}
					message.add(activity.getResources().getString(R.string.network_error));
					adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, message);
					listView.setAdapter(adapter);
				}
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
            
            return theRSSHandler.getFeed();
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
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
