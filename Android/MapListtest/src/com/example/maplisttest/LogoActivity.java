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

/*
 * @author      Academia Sinica
 * @version     $Revision: 1.0 $, $Date:  2013/11/05  $
 * @since    
 * @class name	LogoActivity   
 */
public class LogoActivity extends Activity 
{
	@Override
	/*
	 * @return N/A
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.logo);
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(3000);
        ImageView img_logo = (ImageView)this.findViewById(R.id.img_logo);
        /* Set logo image */
        img_logo.setAnimation(animation);
        /* Set image's animation */
        
        /*
         * @return N/A 
         * @param animation 
         * animation設定傾聽
         */
        animation.setAnimationListener(new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				 Intent intent = new Intent(LogoActivity.this,MainActivity.class); 
			     startActivity(intent);
			     LogoActivity.this.finish();
			}
			/*
			 * (non-Javadoc)
			 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat(android.view.animation.Animation)
			 */
			public void onAnimationRepeat(Animation animation){
				// TODO Auto-generated method stub
			}
			/*
			 * (non-Javadoc)
			 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
			 */
			public void onAnimationStart(Animation animation){
				// TODO Auto-generated method stub
			}
		});
        
	}
       
}
