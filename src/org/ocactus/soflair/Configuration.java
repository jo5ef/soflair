package org.ocactus.soflair;

import java.net.URI;
import java.net.URISyntaxException;

import org.ocactus.soflair.so.FlairInfo;
import org.ocactus.soflair.so.SOHelper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Configuration extends Activity implements OnClickListener {
    
	private static final int PROGRESS_DIALOG = 0;
	private ProgressDialog progressDialog;
	
	private Preferences config;
	private EditText userFlairUrl;
	private Button ok;
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	private Handler errorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dismissDialog(PROGRESS_DIALOG);
			Toast.makeText(Configuration.this, "couldn't download user flair", 2000).show();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.configuration);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			appWidgetId = extras.getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
		
		config = new Preferences(this);
		userFlairUrl = (EditText) findViewById(R.id.userFlairUrl);
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		try {
			URI uri = new URI(userFlairUrl.getText().toString());
			config.setFlairUri(appWidgetId, uri);
		} catch(URISyntaxException ex) {
			Log.e("SO", "didn't work: " +  userFlairUrl.getText().toString());
		}
		
		new Thread(new Runnable() {
			public void run() {
				URI uri = config.getFlairUri(appWidgetId);
				FlairInfo flairInfo = uri == null ? null : SOHelper.loadFlair(Configuration.this, uri);
				
				if(flairInfo == null) {
					errorHandler.sendEmptyMessage(0);
				} else {
					SOWidget.updateWidget(Configuration.this, appWidgetId, flairInfo);
					Intent resultValue = new Intent();
					resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
					setResult(RESULT_OK, resultValue);
					finish();
				}
			}
		}).start();
		showDialog(PROGRESS_DIALOG);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case PROGRESS_DIALOG:
			if(progressDialog == null) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("downloading user flair");
			}
			return progressDialog;
		}
		return super.onCreateDialog(id);
	}
}