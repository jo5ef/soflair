package org.ocactus.soflair;

import java.net.URI;

import org.ocactus.soflair.so.FlairInfo;
import org.ocactus.soflair.so.SOHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	
	private static String TAG = UpdateService.class.getCanonicalName();
	
	private class WidgetUpdater extends Thread {
		
		private int[] appWidgetIds;
		private Context context;
		
		public WidgetUpdater(Context context, int[] appWidgetIds) {
			this.appWidgetIds = appWidgetIds;
			this.context = context;
		}
		
		@Override
		public void run() {
			Preferences config = new Preferences(context);
			for(int appWidgetId : appWidgetIds) {
            	URI flairUri = config.getFlairUri(appWidgetId);
    			if( flairUri == null) {
    				Log.w(TAG, "no user configured for widget (" + appWidgetId + ")");
    				continue;
    			}
    			
    			FlairInfo flair = SOHelper.loadFlair(context, flairUri);
   	    		if(flair != null) {
   	    			SOWidget.updateWidget(context, appWidgetId, flair);
   	    		}
            }
		}
	}
	
	private WidgetUpdater widgetUpdater;
	
	@Override
	public void onStart(Intent intent, int startId) {
		
		Bundle extras = intent.getExtras();
		if(extras != null) {
			int[] appWidgetIds = extras.getIntArray("widgetIds");
			widgetUpdater = new WidgetUpdater(this, appWidgetIds);
			widgetUpdater.start();
		}
	}
	
	@Override
	public void onDestroy() {
		widgetUpdater.stop();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}