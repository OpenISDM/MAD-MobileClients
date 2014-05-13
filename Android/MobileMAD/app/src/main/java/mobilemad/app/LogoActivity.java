package mobilemad.app;

/**
 * Copyright (c) 2014  OpenISDM
 *
 * Project Name:
 *   Mobile Clients for MAD
 *
 * Version:
 *   1.0
 *
 * File Name:
 *   LogoActivity.java
 *
 * Abstract:
 *   LogoActivity.java is the Start-up Programs in Mobile Clients for MAD project.
 *   It will show the opening screen with MAD images.
 *   After that, it will disappear and show the MainActivity.
 *
 * Authors:
 *   Andre Lukito, routhsauniere@gmail.com
 *
 * License:
 *  GPL 3.0 This file is subject to the terms and conditions defined
 *  in file 'COPYING.txt', which is part of this source code package.
 *
 * Major Revision History:
 *   2014/5/08: complete version 1.0
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class LogoActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

    /**
     * Set FullScreen window for splash screen.
     * Set animation image.
     */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_logo);
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
    animation.setDuration(3000);
    ImageView img_logo = (ImageView)this.findViewById(R.id.img_logo);
    img_logo.setAnimation(animation);

    animation.setAnimationListener(new AnimationListener(){
			public void onAnimationEnd(Animation animation){

        /**
         * Start new Activity with the MainActivity.
         */
        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
        startActivity(intent);
        LogoActivity.this.finish();
			}

			public void onAnimationRepeat(Animation animation){
				// TODO Auto-generated method stub
			}

			public void onAnimationStart(Animation animation){
				// TODO Auto-generated method stub
			}
		});
	}
}
