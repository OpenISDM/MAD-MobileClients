package com.example.maplisttest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.res.Configuration;

/**
 * @author      Academia Sinica
 * @version     $Revision: 1.0 $, $Date:  2013/11/05  $
 * @since       
 * @classname	DownloadActivity
 */

public class DownloadActivity extends Activity {
	
	private Button download_file_bt;
	@Override
	/*
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @return N/A
	 * 程式進入點
	 */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		download_file_bt=(Button)findViewById(R.id.download_file_bt);
		download_file_bt.setOnClickListener(new DownFileClickListener());
	}
	
	 class DownFileClickListener implements OnClickListener{  
	       
		 @Override  
	        public void onClick(View v) {  
	            String urlStr="http://140.109.21.188/facilities.json";  
	            try {  
	                /* 
	                 * 通过URL取得HttpURLConnection 
	                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置 
	                 * <uses-permission android:name="android.permission.INTERNET" /> 
	                 */  
	                URL url=new URL(urlStr);  
	                HttpURLConnection conn=(HttpURLConnection)url.openConnection();  
	                //取得inputStream，并进行读取  
	                InputStream input=conn.getInputStream();  
	                BufferedReader in=new BufferedReader(new InputStreamReader(input));  
	                String line=null;  
	                StringBuffer sb=new StringBuffer();  
	              /*  while((line=in.readLine())!=null){  
	                    sb.append(line);  
	                }  
	                System.out.println(sb.toString());  */
	                  
	            } catch (MalformedURLException e) {  
	                e.printStackTrace();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }

		
	    }  
    @Override
    /*
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     * 螢幕轉向
     */
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if(newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE){
            }
        else if(newConfig.orientation ==Configuration.ORIENTATION_PORTRAIT){
            }       
    }

}
