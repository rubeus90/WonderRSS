package com.android.wonderrss.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.android.wonderrss.Adapter.CustomListAdapter;
import com.android.wonderrss.Entities.Feed;
import com.android.wonderrss.Entities.FeedArticle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;

//AsyncTask class to download the thumbnail image of each article
public class ImageService extends AsyncTask<Feed, Integer, Void> {
	
	private boolean boo = false;
	private CustomListAdapter adapter;
	private List<HashMap<String, Object>> map;
	private Feed feed;
	
	public ImageService(CustomListAdapter adapter, List<HashMap<String, Object>> map){
		this.adapter = adapter;
		this.map = map;
	}

	@Override
	protected Void doInBackground(Feed... params) {
		Log.v("Image Service", "Start doInBackground");
		int counter = -1;
		feed = params[0];
		for (final FeedArticle article : feed.getListe()) {
			boo = false;
			counter++;

			//XML doesn't have a "enclosure" tag -> the image's URL is included in the HTML
			if (article.getImageUrl() == null) {
				// We retrieve the article with the URL included in the HTML
				String htmlBody = article.getContent();
				Html.fromHtml(htmlBody, new ImageGetter() {
					@Override
					public Drawable getDrawable(String src) {
						//Use a boolean to retrieve just the first image of each article
						if (!boo) {
							if (src.contains(".png") || src.contains(".jpg")) {
								article.setImageUrl(src);
								Bitmap image = getBitmapFromURL(src);
								if (image != null) {
									article.setImage(image);
									boo = true;
								}
							}
						}
						return null;
					}
				}, null);

			}
			// If the image's URL is included in the "enclosure" tag
			else {
				article.setImage(getBitmapFromURL(article.getImageUrl()));
			}
			
			//Show each image once it's loaded
			publishProgress(counter);
		}
		return null;
	}
	
	//Add the image loaded to the list and notify our CustomAdapter
	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.i("Image Service", "onProgressUpdate : new image loaded");
		super.onProgressUpdate(values);
		map.get(values[0]).put("image", feed.getListe().get(values[0]).getImage());
		adapter.notifyDataSetChanged();
	}

	//Calculate the size of the image to be loaded (prevent memory leak due to large bitmap loading)
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	//Load the bitmap in the size calculated
	public static Bitmap decodeSampledBitmapFromUrl(URL url, int reqWidth,
			int reqHeight) {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			input.close();

			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			input = connection.getInputStream();

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(input, null, options);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Image Service", "Failed to decode the bitmap with the size selected");
		}
		return null;
	}

	//Retrive the bitmap from its URL (in sample size calculated)
	public Bitmap getBitmapFromURL(String src) {
		Log.v("Image Service", "Decode image to bitmap");
		try {
			URL url = new URL(src);
			Bitmap myBitmap = decodeSampledBitmapFromUrl(url, 150, 150);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Image Service", "Decoding image to bitmap failed");
			return null;
		}
	}
}