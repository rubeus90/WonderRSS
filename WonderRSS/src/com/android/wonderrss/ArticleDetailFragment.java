package com.android.wonderrss;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ArticleDetailFragment extends Fragment {
	FeedArticle article;

	public ArticleDetailFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment	
		View view = inflater.inflate(R.layout.article_detail_fragment, container, false);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView date = (TextView) view.findViewById(R.id.pubDate);
		TextView content = (TextView) view.findViewById(R.id.content);
		
		Bundle bundle = getActivity().getIntent().getExtras();
		int position = bundle.getInt("position");
		article = RssService.stream.getListe().get(position);
		
		//Supprimer tous les images dans le code HTML
		String htmlBody = article.getContent().replaceAll("<img.+/(img)*>", "");
		
		title.setText(article.getTitle());
		date.setText(article.getPubDate());
		try{
			content.setText(Html.fromHtml(htmlBody));
		}
		catch(Exception e){
			e.getStackTrace();
			content.setText("An error has occured. No content has been downloaded.");
		}
		
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(article.getUrl()));
				startActivity(i);
			}
		});
		
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
