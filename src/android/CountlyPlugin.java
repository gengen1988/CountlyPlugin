package com.eteng.mobile.countly;

import ly.count.android.api.Countly;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class CountlyPlugin extends CordovaPlugin {
	
	public static final String TAG = "CountlyPlugin";
	
	public static final String META_SERVER = "countly_server";
	public static final String META_APP_KEY = "countly_app_key";
	
	public static final String ACTION_TRACK = "track";
	
	private boolean isReady = false;
	
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		Context context = cordova.getActivity();
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle meta = info.metaData;
			
			String server = meta.getString(META_SERVER);
			String appKey = meta.getString(META_APP_KEY);
			
			Log.d(TAG, "=== countly plugin init ===");
			Log.i(TAG, "countly server: " + server);
			Log.i(TAG, "countly app key: " + appKey);
			Countly.sharedInstance().init(context, server, appKey);
			
			Countly.sharedInstance().onStart();
			
			isReady = true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		super.initialize(cordova, webView);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (!isReady) {
			Log.e(TAG, "countly server and appkey are not ready");
			return false;
		}
		
		if (ACTION_TRACK.equals(action)) {
			return trackEvent(args, callbackContext); 
		}
		
		return false;
	}
	
	private boolean trackEvent(JSONArray args, CallbackContext callbackContext) {
		cordova.getThreadPool().execute(new TrackTask(args, callbackContext));
		return true;
	}

	@Override
	public void onPause(boolean multitasking) {
		if (!isReady) {
			Log.e(TAG, "countly server and appkey are not ready");
			return;
		}
		
		Log.d(TAG, "=== plugin on pause ===");
		Countly.sharedInstance().onStop();
		
		super.onPause(multitasking);
	}

	@Override
	public void onResume(boolean multitasking) {
		if (!isReady) {
			Log.e(TAG, "countly server and appkey are not ready");
			return;
		}
		
		Log.d(TAG, "=== plugin on resume ===");
		Countly.sharedInstance().onStart();
		
		super.onResume(multitasking);
	}
	
}
