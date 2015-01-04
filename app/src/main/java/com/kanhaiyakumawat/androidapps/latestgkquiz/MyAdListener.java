package com.kanhaiyakumawat.androidapps.latestgkquiz;

import android.util.Log;

import com.google.android.gms.ads.AdListener;

public class MyAdListener extends AdListener {
	private static final String LOG = "MyAdListener";
	public void onAdLoaded(){
		Log.v(LOG, "Ad Loaded");
	}
	  public void onAdFailedToLoad(int errorCode){
		  Log.v(LOG, "Ad Failed to Load with error code " + errorCode);
	  }
	  public void onAdOpened()
	  {
		  Log.v(LOG, "Ad Opened");
	  }
	  public void onAdClosed(){
		  Log.v(LOG, "Ad Closed");
	  }
	  public void onAdLeftApplication(){
		  Log.v(LOG, "Ad Left Application");
	  }
}
