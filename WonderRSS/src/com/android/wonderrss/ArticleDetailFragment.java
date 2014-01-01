package com.android.wonderrss;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleDetailFragment extends Fragment {
	FeedArticle article;
	TextView title, date, content;
	ImageView thumbnail;
	View view;

	public ArticleDetailFragment() {
		setHasOptionsMenu(true);
		setRetainInstance(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment	
		view = inflater.inflate(R.layout.article_detail_fragment, container, false);
		
		title = (TextView) view.findViewById(R.id.title);
		date = (TextView) view.findViewById(R.id.pubDate);
		content = (TextView) view.findViewById(R.id.content);
		thumbnail = (ImageView) view.findViewById(R.id.image);
		
		int position = 0;
		try{
			Bundle bundle = getActivity().getIntent().getExtras();
			position = bundle.getInt("position");
			updateContent(position);
		}
		catch(Exception e){
			e.getStackTrace();
		}
		
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(article.getUrl() != null){
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(article.getUrl()));
					startActivity(i);
				}
			}
		});
		
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void updateContent(int position){		
		article = RssService.stream.getListe().get(position);
		
		title.setText(article.getTitle());
		date.setText(article.getPubDate());	
		
		//Supprimer tous les images dans le code HTML
//		String htmlBody = article.getContent().replaceAll("<img.+/(img)*>", "");	
		String htmlBody = article.getContent().replaceAll("<img.+?>", "");
		try{
			content.setText(Html.fromHtml(htmlBody));
//			content.setText(htmlBody);
			
			content.setMovementMethod(LinkMovementMethod.getInstance()); //pour que les liens deviennent cliquables
		}
		catch(Exception e){
			e.getStackTrace();
			Log.e("ArticleDetailFragment", "Erreur transformer le code HTML en texte");
			content.setText("An error has occured. No content has been downloaded.");
		}
		
		thumbnail.setImageBitmap(article.getImage());
	}
}
