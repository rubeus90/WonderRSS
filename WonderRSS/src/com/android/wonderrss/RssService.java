package com.android.wonderrss;

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

public class RssService	extends AsyncTask<Map<String,?>, Void, Feed> {
	
	private Activity activity;
	private ArticleListFragment fragment;
	private RssParser theRSSHandler;
	private CustomListAdapter adapter;
	static Feed stream;
	private ProgressDialog progress;

	public ProgressDialog getProgress() {
		return progress;
	}

	public RssService(ArticleListFragment fragment) {	
		this.fragment = fragment;
		activity = fragment.getActivity();
	}

	public void onPreExecute() {
		Log.v("Rss Service", "onPreExecute");		
		progress = new ProgressDialog(activity);
		progress.setMessage(activity.getResources().getString(R.string.downloading));
		progress.show();
	}

	public void onPostExecute(final Feed feed) {
		Log.v("Rss Service", "onPostExecute");
				
		List<FeedArticle> list = feed.getListe();					
		HashMap<String, Object> map;
		List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
		
		/****On ajoute les proprietes de chaque article dans un HashMap*****/
		for (final FeedArticle article : list) {					
			map = new HashMap<String, Object>();
			map.put("title", article.getTitle());
			map.put("date", article.getPubDate() + " by " + article.getAuthor());
			
			listMap.add(map);
		}
		adapter = new CustomListAdapter(activity, listMap);
		fragment.setListAdapter(adapter);	
		Log.v("Rss Service", "On ajoute l'adapter au fragment");
		
		if(progress != null)
			progress.dismiss();
		
		/***** On lance le AsyncTask pour telecharger des images *********/
		ImageService imageService = new ImageService(adapter, listMap);
		imageService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, feed);
	}

	@Override
	protected Feed doInBackground(Map<String,?>... arg0) {
		stream = new Feed();
		
		Log.v("Rss Service", "On commence a recuperer le XML");		
		try {
			URL url = null;
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
            
            Log.v("Rss Service", "On a reussi a recuperer le XML");
            
            Log.v("Rss Service", "On recupere les images des articles");
//            setArticleImage();
            Log.v("Rss Service", "Les images des articles ont ete recuperees");
            
            return stream;
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.e("Rss Service", "On a pas reussi a recuperer le XML");
            return stream;
        }
	}
}
