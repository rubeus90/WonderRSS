package com.android.wonderrss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RssParser extends DefaultHandler{
	
	private FeedArticle currentArticle = new FeedArticle();
	private Feed feed = new Feed();
	StringBuffer buffer;
	String url;
	String imageUrl;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		buffer = new StringBuffer();
		
		if(localName.equalsIgnoreCase("link")){
			if(attributes.getValue("href") != null)
				url = attributes.getValue("href").toString();
		}
		if(localName.equalsIgnoreCase("enclosure")){
			if(attributes.getValue("url") != null){
				imageUrl = attributes.getValue("url").toString();
			}
		}

	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(localName.equalsIgnoreCase("title")){
			currentArticle.setTitle(buffer.toString());
		}
		else if(localName.equalsIgnoreCase("author")){
			currentArticle.setAuthor(buffer.toString());
		}
		else if(localName.equalsIgnoreCase("pubDate") || localName.equalsIgnoreCase("published")){
			currentArticle.setPubDate(buffer.toString());
		}
		else if(localName.equalsIgnoreCase("guid")){
			currentArticle.setGuid(buffer.toString());
		}
		else if(localName.equalsIgnoreCase("content") || localName.equalsIgnoreCase("description")){
			currentArticle.setContent(buffer.toString());
		}
		else if(localName.equalsIgnoreCase("link")){
			String string = buffer.toString();
			if(! string.equals(""))
				currentArticle.setUrl(string);	
			else
				currentArticle.setUrl(url);
		}
		else if(localName.equalsIgnoreCase("enclosure")){
			currentArticle.setImageUrl(imageUrl);
		}
		
		if(localName.equalsIgnoreCase("entry") || localName.equalsIgnoreCase("item")){			
			feed.addArticle(currentArticle);	
			currentArticle = new FeedArticle();
		}
	}
	
	public void characters(char ch[], int start, int length) {
		buffer.append(new String(ch, start, length));
	}

	public Feed getFeed() {	
		return feed;
	}	
}
