package com.android.wonderrss;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.DocumentsContract.Document;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

public class RssService	extends AsyncTask<String, Void, Feed> {
	
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
				for (final FeedArticle article : list) {
					/****On ajoute les proprietes de chaque article dans un HashMap*****/
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
	protected Feed doInBackground(String... arg0) {
		Log.v("Rss Service", "On commence a recuperer le XML");
		try {
            URL url= new URL(arg0[0]);
            
            SAXParserFactory factory =SAXParserFactory.newInstance();
            SAXParser parser=factory.newSAXParser();
            XMLReader xmlreader=parser.getXMLReader();
            
            theRSSHandler=new RssParser();
            xmlreader.setContentHandler(theRSSHandler);
            InputSource is=new InputSource(url.openStream());
            
            xmlreader.parse(is);
            Feed newFeed = theRSSHandler.getFeed();
            stream.addFeed(newFeed);
            Log.v("Rss Service", "On a reussi a recuperer le XML");
            
            Log.v("Rss Service", "On recupere les images des articles");
            setArticleImage();
            Log.v("Rss Service", "Les images des articles ont ete recuperees");
            
            return stream;
        } catch (Exception e) {
        	e.printStackTrace();
        	if(!isConnectedToInternet())
        		Toast.makeText(activity, "No internet connection!", Toast.LENGTH_SHORT).show();
        	if(stream == null)
        		stream = new Feed();
        	Log.e("Rss Service", "On a pas reussi a recuperer le XML");
            return stream;
        }
	}
	
	public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(ListActivity.CONNECTIVITY_SERVICE);
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
