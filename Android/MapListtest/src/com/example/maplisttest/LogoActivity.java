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


public class LogoActivity extends Activity {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示
		setContentView(R.layout.logo);
		//这里用到AlphaAnimation类，实现动画的
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);//0.0f到1.0f为完全透明到完全不透明
        animation.setDuration(3000);//设置动画持续时间为5秒
        
        ImageView img_logo = (ImageView) this.findViewById(R.id.img_logo);//实例化logo（耳机）
        img_logo.setAnimation(animation);
        
        //重写监听类，让activity在动画结束时即onAnimationEnd时，跳转
        animation.setAnimationListener(new AnimationListener(){

			public void onAnimationEnd(Animation animation)
			{

				 Intent intent = new Intent(LogoActivity.this, MainActivity.class);
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
