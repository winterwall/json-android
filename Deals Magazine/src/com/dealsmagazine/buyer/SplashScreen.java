/*
 * Copyright (C) 2012  | http://www.dealsmagazine.com 
 * © 2012 Deals Magazine Inc.
 *
 */

package com.dealsmagazine.buyer;

/*
 * Splash Screen Activity
 * 
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dealsmagazine.globals.Globals;

public class SplashScreen extends Activity {
	protected boolean _active = true;

	// Setup the splash screen time
	protected int _splashTime = Globals.SPLASH_TIME;

	private ImageView iv_splash;

	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.splash);

		iv_splash = (ImageView) this.findViewById(R.id.iv_splash);
		Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
		iv_splash.startAnimation(myFadeInAnimation);

		// Add other effect to Welcome Screen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					// TODO: handle exception
				} finally {
					Intent i = new Intent();
					i.setClass(SplashScreen.this, DealsMagazineActivity.class);
					i.putExtra(DealsMagazineActivity.KEY_SYNC, 1);
					startActivity(i);
					finish();
				}
			}
		};
		splashTread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_active = false;
		}
		return true;
	}
}
