package com.android.wonderrss;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        if(convertView==null)
            view = inflater.inflate(R.layout.list_row, null);
        
        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        TextView title = (TextView) view.findViewById(R.id.list_title);
        TextView dateAuthor = (TextView) view.findViewById(R.id.list_date);
        
        title.setText((String)map.get(position).get("title"));
        dateAuthor.setText((String)map.get(position).get("date"));
        thumbnail.setImageBitmap((Bitmap) map.get(position).get("image"));
        
		return view;
	}
}
