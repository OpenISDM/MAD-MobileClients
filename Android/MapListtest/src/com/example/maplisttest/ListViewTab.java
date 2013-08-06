package com.example.maplisttest;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;




public class ListViewTab extends FragmentActivity{
    private TabHost mTabHost;
    private TabManager mTabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost1);
        
        Config.context = this;
        
        mTabHost = (TabHost)findViewById(R.id.TabHost1);
        mTabHost.setup();
        
        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontents);

        mTabManager.addTab(
                mTabHost.newTabSpec("All").setIndicator("All"),
                AllListView.class, null);
        mTabManager.addTab(
            mTabHost.newTabSpec("Shelter").setIndicator("Shelter"),
            ShelterListView.class, null);
        mTabManager.addTab(
            mTabHost.newTabSpec("Hospital").setIndicator("Hospital"),
            HospitalListView.class, null);
        mTabManager.addTab(
            mTabHost.newTabSpec("FireStation").setIndicator("FireStation"),
            FireStationListView.class, null);
        mTabManager.addTab(
            mTabHost.newTabSpec("Police").setIndicator("Police"),
            PoliceListView.class, null);
       
       
        
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm); //先取得螢幕解析度  
        int screenWidth = dm.widthPixels;   //取得螢幕的寬
           
           
        TabWidget tabWidget = mTabHost.getTabWidget();   //取得tab的物件
        int count = tabWidget.getChildCount();   //取得tab的分頁有幾個
        if (count > 3) {   
            for (int i = 0; i < count; i++) {   
                tabWidget.getChildTabViewAt(i)
                      .setMinimumWidth((screenWidth)/6);//設定每一個分頁最小的寬度   
            }   
        }
        
        Button button = (Button)findViewById(R.id.button01); 
        button.setOnClickListener(new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
        // TODO Auto-generated method stub 
       
        	Intent intent = new Intent();
            intent.setClass(ListViewTab.this, MapMainActivity.class);
            startActivity(intent); 
 
        ListViewTab.this.finish();
        }
        });
        
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
