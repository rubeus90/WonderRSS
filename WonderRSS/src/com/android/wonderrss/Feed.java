package com.android.wonderrss;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Feed {
	private List<FeedArticle> liste;
	private String title;
	private String description;
	private URL url;

	public Feed() {
		liste = new ArrayList<FeedArticle>();
	}

	public List<FeedArticle> getListe() {
		return liste;
	}

	public void setListe(List<FeedArticle> liste) {
		this.liste = liste;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void addArticle(FeedArticle article) {
		liste.add(article);
	}
}

