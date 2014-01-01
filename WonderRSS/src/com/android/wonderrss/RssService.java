package com.android.wonderrss;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.ListAdapter;

public class RssService	extends AsyncTask<Map<String,?>, Void, Feed> {
	
	private Activity activity;
	private ArticleListFragment fragment;
	private RssParser theRSSHandler;
	private ListAdapter adapter;
	private ProgressDialog progress;
	static Feed stream;
	private boolean boo = false;

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
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				List<FeedArticle> list = feed.getListe();					
				HashMap<String, Object> map;
				List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
				
				/****On ajoute les proprietes de chaque article dans un HashMap*****/
				for (final FeedArticle article : list) {					
					map = new HashMap<String, Object>();
					map.put("title", article.getTitle());
					map.put("date", article.getPubDate() + " by " + article.getAuthor());
					map.put("image", article.getImage());
					
					listMap.add(map);
				}
				adapter = new CustomListAdapter(activity, listMap);
				fragment.setListAdapter(adapter);	
				Log.v("Rss Service", "On ajoute l'adapter au fragment");
			}
		});
		
		progress.dismiss();
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
            setArticleImage();
            Log.v("Rss Service", "Les images des articles ont ete recuperees");
            
            return stream;
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.e("Rss Service", "On a pas reussi a recuperer le XML");
            return stream;
        }
	}
	
	public Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e("ArticleDetailFragment", "Probleme recuperer le bitmap a partir du URL");
	        return null;
	    }
	}
	
	public void setArticleImage(){
		for(final FeedArticle article : stream.getListe()){
			boo = false;
			
			//Si le XML n'a pas de champ "enclosure" et le lien de l'image est contenu dans le HTML
			if(article.getImageUrl() == null){
				//On recupere l'image de l'article avec le lien dans le content
				String htmlBody = article.getContent();
				Html.fromHtml(htmlBody, new ImageGetter() {
				    @Override
				    public Drawable getDrawable(String src) {
				    	if(!boo){
				    		if(src.contains(".png") || src.contains(".jpg")){
					    		
				    			Bitmap image = getBitmapFromURL(src);
				    				if(image != null){
				    					article.setImage(image);
				    					boo = true;
				    				}	
					    	}
				    	}				    		
				    	return null;
				    }
				}, null);	
				
			}
			//Si le XML a un champ "enclosure" qui contient le lien de l'image
			else{
				article.setImage(getBitmapFromURL(article.getImageUrl()));
			}
        }
	}
}
