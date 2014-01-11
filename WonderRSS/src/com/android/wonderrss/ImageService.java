package com.android.wonderrss;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;

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
		Log.i("AsyncTask image", "doInBackground");
		int counter = -1;
		feed = params[0];
		for (final FeedArticle article : feed.getListe()) {
			boo = false;
			counter++;

			// Si le XML n'a pas de champ "enclosure" et le lien de l'image est
			// contenu dans le HTML
			if (article.getImageUrl() == null) {
				// On recupere l'image de l'article avec le lien dans le content
				String htmlBody = article.getContent();
				Html.fromHtml(htmlBody, new ImageGetter() {
					@Override
					public Drawable getDrawable(String src) {
						if (!boo) {
							if (src.contains(".png") || src.contains(".jpg")) {

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
			// Si le XML a un champ "enclosure" qui contient le lien de l'image
			else {
				article.setImage(getBitmapFromURL(article.getImageUrl()));
			}
			publishProgress(counter);
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.i("AsyncTask image", "onProgressUpdate");
		super.onProgressUpdate(values);
		map.get(values[0]).put("image", feed.getListe().get(values[0]).getImage());
		System.out.println(values[0]);
		adapter.notifyDataSetChanged();
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		System.out.println("largeur = " + options.outWidth);
		System.out.println("hauteur = " + options.outHeight);
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

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
		}
		return null;
	}

	public Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			Bitmap myBitmap = decodeSampledBitmapFromUrl(url, 150, 150);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("ArticleDetailFragment",
					"Probleme recuperer le bitmap a partir du URL");
			return null;
		}
	}
}
