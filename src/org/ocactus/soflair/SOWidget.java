package org.ocactus.soflair;

import org.ocactus.soflair.so.FlairInfo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.widget.RemoteViews;

public class SOWidget extends AppWidgetProvider {

	private static final String BADGE = " \u25cf";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Intent serviceIntent = new Intent(context, UpdateService.class);
		serviceIntent.putExtra("widgetIds", appWidgetIds);
		context.startService(serviceIntent);
	}
	
	public static void updateWidget(Context context, int appWidgetId, FlairInfo flair) {
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		
		setOnClick(context, remoteViews, flair);
		
		remoteViews.setTextViewText(R.id.displayName, flair.getDisplayName());
		if(flair.getAvatar() != null) {
			remoteViews.setBitmap(R.id.avatar, "setImageBitmap", flair.getAvatar());
		}
		
		remoteViews.setTextViewText(R.id.reputation, flair.getReputationDisplayString());
		
		SpannableStringBuilder sb = new SpannableStringBuilder();
		
		appendBadge(sb, new TextAppearanceSpan(context, R.style.goldBadge), flair.getNumberOfGoldBadges());
		appendBadge(sb, new TextAppearanceSpan(context, R.style.silverBadge), flair.getNumberOfSilverBadges());
		appendBadge(sb, new TextAppearanceSpan(context, R.style.bronzeBadge), flair.getNumberOfBronzeBadges());
		
		remoteViews.setTextViewText(R.id.creds, sb);
		manager.updateAppWidget(appWidgetId, remoteViews);
	}
	
	private static void setOnClick(Context context, RemoteViews remoteViews, FlairInfo flair) {
		if(flair.getProfileUrl() != null) {
			Intent viewProfile = new Intent(Intent.ACTION_VIEW, Uri.parse(flair.getProfileUrl().toString()));
			PendingIntent viewProfileActivity = PendingIntent.getActivity(context, 0, viewProfile, 0);
			remoteViews.setOnClickPendingIntent(R.id.widget, viewProfileActivity);
		}
	}
	
	private static void appendBadge(SpannableStringBuilder sb, Object style, int number) {
		if(number > 0) {
			appendStyled(sb, style, BADGE);
			sb.append(number + "");
		}
	}
	
	private static void appendStyled(SpannableStringBuilder sb, Object style, String text) {
		int s = sb.length();
		sb.append(text);
		sb.setSpan(style, s, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
}

