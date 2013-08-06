package com.example.maplisttest;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;


public class DownloadActivity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.download);
				

	

	}
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if(newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE){
            
        }else if(newConfig.orientation ==Configuration.ORIENTATION_PORTRAIT){
            
        }
    }

}
