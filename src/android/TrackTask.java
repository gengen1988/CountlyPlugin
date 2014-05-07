package com.eteng.mobile.countly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ly.count.android.api.Countly;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TrackTask implements Runnable {
	
	public static final String TAG = "CountlyTask";
	
	CallbackContext callback;
	JSONArray args;
	
	public static final String OPTS_KEY = "key";
	public static final String OPTS_SEGMENT = "segment";
	public static final String OPTS_COUNT = "count";
	public static final String OPTS_SUM = "sum";
	
	TrackTask(JSONArray args, CallbackContext callback) {
		this.args = args;
		this.callback = callback;
	}

	@Override
	public void run() {
		JSONObject opts;
		try {
			opts = args.getJSONObject(0);
		} catch (JSONException e) {
			callback.error(e.getMessage());
			return;
		}
		
		String key;
		Integer count;
		Double sum;
		JSONObject segment;
		
		try {
			key = opts.getString(OPTS_KEY);
		} catch (JSONException e) {
			key = "undefined";
		}
		
		try {
			count = opts.getInt(OPTS_COUNT);
		} catch (JSONException e) {
			count = null;
		}
		
		try {
			sum = opts.getDouble(OPTS_SUM);
		} catch (JSONException e) {
			sum = null;
		}
		
		try {
			segment = opts.getJSONObject(OPTS_SEGMENT);
		} catch (JSONException e) {
			segment = null;
		}
		
		if (segment != null) {
			if (count == null) {
				count = 1;
			}
			if (sum != null) {
				recordEvent(key, segment, count, sum);
				callback.success();
				return;
			}
			recordEvent(key, segment, count);
			callback.success();
			return;
		}
		
		if (sum != null) {
			recordEvent(key, count, sum);
			callback.success();
			return;
		}
		
		if (count != null) {
			recordEvent(key, count);
			callback.success();
			return;
		}
		
		recordEvent(key);
		callback.success();
	}

	private void recordEvent(String key) {
		Log.d(TAG, "record event");
		Log.d(TAG, "key: " + key);
		Countly.sharedInstance().recordEvent(key);
	}
	
	private void recordEvent(String key, Integer count) {
		Log.d(TAG, "record event");
		Log.d(TAG, "key: " + key);
		Log.d(TAG, "count: " + count);
		Countly.sharedInstance().recordEvent(key, count);
	}
	
	private void recordEvent(String key, Integer count, Double sum) {
		Log.d(TAG, "record event");
		Log.d(TAG, "key: " + key);
		Log.d(TAG, "count: " + count);
		Log.d(TAG, "sum: " + sum);
		Countly.sharedInstance().recordEvent(key, count, sum);
	}
	
	private void recordEvent(String key, JSONObject json, Integer count) {
		Log.d(TAG, "record event");
		Log.d(TAG, "key: " + key);
		Log.d(TAG, "segment: " + json);
		Log.d(TAG, "count: " + count);
		Countly.sharedInstance().recordEvent(key, JSONToSegmentation(json), count);
	}
	
	private void recordEvent(String key, JSONObject json, Integer count, Double sum) {
		Log.d(TAG, "record event");
		Log.d(TAG, "key: " + key);
		Log.d(TAG, "segment: " + json);
		Log.d(TAG, "count: " + count);
		Log.d(TAG, "sum: " + sum);
		Countly.sharedInstance().recordEvent(key, JSONToSegmentation(json), count, sum);
	}
	
	private Map<String, String> JSONToSegmentation(JSONObject json) {
		@SuppressWarnings("unchecked")
		Iterator<String> it = json.keys();
		Map<String, String> result = new HashMap<String, String>();
		
		while (it.hasNext()) {
			String name = it.next();
			try {
				result.put(name, json.getString(name));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
