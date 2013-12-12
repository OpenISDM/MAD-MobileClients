package com.example.maplisttest;

import android.os.Bundle;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Window;
import android.widget.TabHost;

/* </SOURCE_HEADER>
 * 
 * <NAME>
 * <Academia Sinica MapListtest>
 * </NAME>
 * 
 * <RCS_KEYWORD>
 * $Source$
 * $Revision: 1.0 $
 * $Date: 2013/11/05 $
 *</RCS_KEYWORD>
 *
 *<COPYRIGHT>
 * The following source code is protected under all standard copyright laws.
 *</COPYRIGHT>
 *
 *</SOURCE_HEADER>
 */

/**
 * @author      Academia Sinica
 * @version     $Revision: 1.0 $, $Date:  2013/11/05  $
 * @since       
 */
@SuppressWarnings("deprecation")
public class MainActivity extends ActivityGroup {
	
	private TabHost mTabHost = null;
	/*
	 * (non-Javadoc)
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
				
		mTabHost = (TabHost) findViewById(R.id.maintabhost);
		
		mTabHost.setup();
		
		mTabHost.setup(this.getLocalActivityManager());
	
		mTabHost.addTab(mTabHost.newTabSpec("t1")
				.setIndicator("DownLoad Information")
				.setContent(new Intent(this, DownloadActivity.class)));//跳至下載頁面
		
		mTabHost.addTab(mTabHost.newTabSpec("t2").setIndicator("Map/List")
				.setContent(new Intent(this, ListViewTab.class)));//跳至地圖頁面
		
		mTabHost.addTab(mTabHost.newTabSpec("t3")
				.setIndicator("Emergency Board")
				.setContent(new Intent(this, EmergencyBoardActivity.class)));//跳至緊急求救頁面
		
		mTabHost.addTab(mTabHost.newTabSpec("t4").setIndicator("Host Server")
				.setContent(new Intent(this, HotSpotActivity.class)));//跳至熱點設定頁面
		
		//mTabHost.setCurrentTabByTag("t2");//將目前的頁面設定誌地圖頁面tab2
	}
	/**************************************************************************
	 *  Method name: onConfigurationChanged
	 *  Functionality: Avoid execute onCreate when orientation change 
	 *  @param: Configuration
	 *  @return: N/A
	**************************************************************************/
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if(newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE){
            }
        else if(newConfig.orientation ==Configuration.ORIENTATION_PORTRAIT){
            }
    }

}
