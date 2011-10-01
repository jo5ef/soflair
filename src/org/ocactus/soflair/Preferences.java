package org.ocactus.soflair;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Preferences {
	
	private static final String TAG = "soflair.config";
	private static final String PREFERENCES = "org.ocactus.soflair";
	private static final String URI_PREFIX = "uri";
	
	private SharedPreferences sharedPreferences;
	
	public Preferences(Context context) {
		sharedPreferences = context.getSharedPreferences(PREFERENCES, 0);
	}
	
	public void setFlairUri(int appWidgetId, URI uri) {
		set(URI_PREFIX + appWidgetId, uri.toString());
	}
	
	public URI getFlairUri(int appWidgetId) {
		try {
			String uri = get(URI_PREFIX + appWidgetId);
			if(uri == null) {
				uri = get("user" + appWidgetId);	// previous version
			}
			return uri == null ? null : new URI(uri);
		} catch(URISyntaxException ex) {
			Log.e(TAG, "invalid url format", ex);
			return null;
		}
	}
	
	private void set(String key, String value) {
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private String get(String key) {
		return sharedPreferences.getString(key, null);
	}
}