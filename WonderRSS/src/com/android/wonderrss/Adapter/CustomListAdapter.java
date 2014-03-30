package com.android.wonderrss.Adapter;

import java.util.HashMap;
import java.util.List;

import com.android.wonderrss.R;
import com.android.wonderrss.R.id;
import com.android.wonderrss.R.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//Custom Adapter for the article list : for each row : an image + article's title + date + author
public class CustomListAdapter extends BaseAdapter{
	private List<HashMap<String, Object>> map;
	private LayoutInflater inflater;
	
	public CustomListAdapter(Context context, List<HashMap<String, Object>> map){
		this.map = map;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return map.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=convertView;
		
		//If there's no view converted, we inflate the layout
        if(convertView==null){
            view = inflater.inflate(R.layout.list_row, null);
            CacheView cache = new CacheView();
            cache.title = (TextView) view.findViewById(R.id.list_title);
            cache.dateAuthor = (TextView) view.findViewById(R.id.list_date);
            cache.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            view.setTag(cache);
        }
        
        //Retrive the items from cache
        CacheView cache = (CacheView) view.getTag();
        
        cache.title.setText((String)map.get(position).get("title"));
        cache.dateAuthor.setText((String)map.get(position).get("date"));
        cache.thumbnail.setImageBitmap((Bitmap) map.get(position).get("image"));
        
		return view;
	}
	
	//Cache
	private static class CacheView{
		public TextView title;
		public TextView dateAuthor;
		public ImageView thumbnail;
	}
}
