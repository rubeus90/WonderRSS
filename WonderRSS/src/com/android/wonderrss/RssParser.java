package com.android.wonderrss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class RssParser extends DefaultHandler{
	
	private FeedArticle currentArticle = new FeedArticle();
	private Feed feed = new Feed();
	private int articlesAdded = 0;
	StringBuffer buffer;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		buffer = new StringBuffer();
//		else if(localName.equalsIgnoreCase("link")){
//		if(!buffer.toString().equals(""))
//			currentArticle.setUrl(buffer.toString());
//		else
//			currentArticle.setUrl(attributes.getValue("href").toString());
//		Log.v("Parser", "Le lien a ete ajoute");
//	}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		Log.v("Parser", "On parse le fichier XML pour remplir les champs de l'article");
		
		if(localName.equalsIgnoreCase("title")){
			currentArticle.setTitle(buffer.toString());
			Log.v("Parser", "Le titre a ete ajoute");
		}
		else if(localName.equalsIgnoreCase("author")){
			currentArticle.setAuthor(buffer.toString());
			Log.v("Parser", "L'auteur a ete ajoute");
		}
		else if(localName.equalsIgnoreCase("pubDate") || localName.equalsIgnoreCase("published")){
			currentArticle.setPubDate(buffer.toString());
			Log.v("Parser", "La date a ete ajoute");
		}
		else if(localName.equalsIgnoreCase("guid")){
			currentArticle.setGuid(buffer.toString());
			Log.v("Parser", "Le GUID a ete ajoute");
		}
		else if(localName.equalsIgnoreCase("content") || localName.equalsIgnoreCase("description")){
			currentArticle.setContent(buffer.toString());
			Log.v("Parser", "Le texte de l'article a ete ajoute");
		}
		
		if(localName.equalsIgnoreCase("entry") || localName.equalsIgnoreCase("item")){			
			feed.addArticle(currentArticle);	
			currentArticle = new FeedArticle();
			articlesAdded++;
			Log.v("Parser", "Un article a ete ajoute au Feed");
		}
	}
	
	public void characters(char ch[], int start, int length) {
		buffer.append(new String(ch, start, length));
	}

	public Feed getFeed() {	
		return feed;
	}	
}
