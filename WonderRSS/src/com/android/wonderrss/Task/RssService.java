package com.android.wonderrss.Task;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.android.wonderrss.ArticleListFragment;
import com.android.wonderrss.R;
import com.android.wonderrss.Adapter.CustomListAdapter;
import com.android.wonderrss.Entities.Feed;
import com.android.wonderrss.Entities.FeedArticle;
import com.android.wonderrss.Parser.RssParser;

//AsyncTask class to load the RSS stream
public class RssService	extends AsyncTask<Map<String,?>, Void, Feed> {
	
	private Activity activity;
	private ArticleListFragment fragment;
	private RssParser theRSSHandler;
	private CustomListAdapter adapter;
	public static Feed stream;
	private ProgressDialog progress;

	public ProgressDialog getProgress() {
		return progress;
	}

	public RssService(ArticleListFragment fragment) {	
		this.fragment = fragment;
		activity = fragment.getActivity();
	}

	//Show a ProgressDialog to notify the user that the stream is being loaded
	public void onPreExecute() {
		Log.v("Rss Service", "onPreExecute : ProgressDialog");		
		progress = new ProgressDialog(activity);
		progress.setMessage(activity.getResources().getString(R.string.downloading));
		progress.show();
	}
	
	@Override
	protected Feed doInBackground(Map<String,?>... arg0) {
		Log.v("Rss Service", "doInBackground : retrieve the XML");	
		
		stream = new Feed();			
		try {
			URL url = null;
			
			//We retrieve each RSS source, then we add all their articles to our stream
			for (Map.Entry<String, ?> entry : arg0[0].entrySet()) {		
				if(entry.getKey() != null){
					url= new URL(entry.getValue().toString());
	            
		            SAXParserFactory factory =SAXParserFactory.newInstance();
		            SAXParser parser=factory.newSAXParser();
		            XMLReader xmlreader=parser.getXMLReader();
		            
		            theRSSHandler=new RssParser();
		            xmlreader.setContentHandler(theRSSHandler);
		            InputSource is=new InputSource(url.openStream());
		            
		            xmlreader.parse(is);
		            Feed newFeed = theRSSHandler.getFeed();
		            stream.addFeed(newFeed);
				}
			}
            
            Log.v("Rss Service", "Finish doInBackground");
            
            return stream;
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.e("Rss Service", "doInBackground : retrieving XML failed");
            return stream;
        }
	}

	//Populate the list using our CustomAdapter
	public void onPostExecute(final Feed feed) {
		Log.v("Rss Service", "Start onPostExecute");
				
		List<FeedArticle> list = feed.getListe();					
		HashMap<String, Object> map;
		List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
		
		//Add all proprieties of an article in an HashMap
		for (final FeedArticle article : list) {					
			map = new HashMap<String, Object>();
			map.put("title", article.getTitle());
			map.put("date", article.getPubDate() + " by " + article.getAuthor());
			
			listMap.add(map);
		}
		//Pass this list to our CustomAdapter
		adapter = new CustomListAdapter(activity, listMap);
		fragment.setListAdapter(adapter);	
		Log.v("Rss Service", "onPostExecute : CustomAdapter added to the fragment");
		
		//Dismiss the ProgressDialog
		if(progress != null)
			progress.dismiss();
		
		//Launch the 2nd AsyncTask to retrieve all the images
		Log.v("Rss Service", "Launch the 2nd AsyncTask to retrieve the images");
		ImageService imageService = new ImageService(adapter, listMap);
		imageService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, feed);
	}
}
