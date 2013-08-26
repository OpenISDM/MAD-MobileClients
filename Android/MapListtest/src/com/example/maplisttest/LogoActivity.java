package com.example.maplisttest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

/******************************************************************************
 *  Class name: LogoActivity
 *  Inheritance: N/A
 *  Methods: N/A
 *  Functionality: Animation the welcome screen  
******************************************************************************/
public class LogoActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.logo);
		
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		
        animation.setDuration(3000);
        
        ImageView img_logo = (ImageView)this.findViewById(R.id.img_logo);
        /* Set logo image */
        
        img_logo.setAnimation(animation);
        /* Set image's animation */
        
        animation.setAnimationListener(new AnimationListener()
        {
			public void onAnimationEnd(Animation animation)
			{
				 Intent intent = new Intent(LogoActivity.this,
						 					MainActivity.class);
				 
			     startActivity(intent);
			     
			     LogoActivity.this.finish();
			}

			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub
			}

			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub
			}
		});
        
	}
       
}
