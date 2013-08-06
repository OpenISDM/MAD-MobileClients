package com.example.maplisttest;

import android.os.Bundle;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Window;
import android.widget.TabHost;


@SuppressWarnings("deprecation")
public class MapMainActivity extends ActivityGroup {

	private TabHost mTabHost = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
				
		mTabHost = (TabHost) findViewById(R.id.maintabhost);
		mTabHost.setup();
		
		
		mTabHost.setup(this.getLocalActivityManager());
	
		mTabHost.addTab(mTabHost.newTabSpec("t1").setIndicator("DownLoad Information")
				.setContent(R.id.tab1));
		mTabHost.addTab(mTabHost.newTabSpec("t2").setIndicator("Map/List")
				.setContent(new Intent(this, MapActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("t3").setIndicator("Emergency Board")
				.setContent(R.id.tab3));
		mTabHost.addTab(mTabHost.newTabSpec("t4").setIndicator("Host Server")
				.setContent(R.id.tab4));
		mTabHost.setCurrentTabByTag("t2");
	

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
