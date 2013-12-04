package com.android.wonderrss;

import android.app.Activity;
import android.os.Bundle;

public class DetailActivity extends Activity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);                
    }
	
	public int getPosition(){
		Bundle bundle = getIntent().getExtras();

        return bundle.getInt("position");
	}
}
