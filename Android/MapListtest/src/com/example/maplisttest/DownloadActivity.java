package com.example.maplisttest;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;

/******************************************************************************
 *  Class name: DownloadActivity
 *  Inheritance: N/A
 *  Methods: onConfigurationChanged
 *  Functionality: Temporary DownloadActivity 
******************************************************************************/
public class DownloadActivity extends Activity 
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.download);

	}
	/**************************************************************************
	 *  Method name: onConfigurationChanged
	 *  Functionality: Avoid execute onCreate when orientation change 
	 *  @param: Configuration
	 *  @return: N/A
	**************************************************************************/
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if(newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE)
        {
            
        }
        else if(newConfig.orientation ==Configuration.ORIENTATION_PORTRAIT)
        {
            
        }
        
    }

}
