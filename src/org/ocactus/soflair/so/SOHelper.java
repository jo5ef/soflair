package org.ocactus.soflair.so;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.DisplayMetrics;
import android.util.Log;

public class SOHelper {

	private static final String TAG = "SOHelper";
	
	public static FlairInfo loadFlair(Context context, URI uri) {
		
		if(uri == null) return null;
		
		try {
			String jsonSource = downloadJson(uri);
			if(jsonSource != null) {
				cacheJson(context, uri, jsonSource);
			} else {
				jsonSource = cachedJson(context, uri);
				Log.i(TAG, String.format("using cached json (%s)", uri));
			}
			
			if(jsonSource == null) {
				Log.e(TAG, "couldn't retrieve json");
				return null;
			}
			
			FlairInfo flair = parseFlair(context, uri, jsonSource);
			
			Bitmap avatar = downloadBitmap(flair.getAvatarURI());
			if(avatar != null) {
				flair.setAvatar(avatar);
				cacheAvatar(context, flair.getAvatarURI(), avatar);
			} else {
				flair.setAvatar(cachedAvatar(context, flair.getAvatarURI()));
				Log.i(TAG, String.format("using cached avatar (%s)", flair.getAvatarURI()));
			}
			
			return flair;
			
		} catch(JSONException ex) {
			Log.e(TAG, String.format("error parsing json (%s)", uri), ex);
			return null;
		}
	}
	
	private static void cacheJson(Context context, URI uri, String jsonSource) {
		try {
			PrintWriter out = new PrintWriter(context.openFileOutput(
				String.format("%d.json", uri.hashCode()), Context.MODE_PRIVATE));
			out.write(jsonSource);
			out.close();
		} catch(IOException ex) {
			Log.e(TAG, String.format("error caching %s", uri));
		}
	}
	
	private static String cachedJson(Context context, URI uri) {
		try {
			StringBuffer json = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(
				context.openFileInput(String.format("%d.json", uri.hashCode()))));
			
			String line = null;
			while((line = in.readLine()) != null) {
				json.append(line);
			}
			in.close();
			return json.toString();
		} catch(IOException ex) {
			Log.e(TAG, String.format("error reading cached %s", uri));
			return null;
		}
	}
	
	private static void cacheAvatar(Context context, URI uri, Bitmap avatar) {
		try {
			avatar.compress(CompressFormat.PNG, 0, 
				context.openFileOutput(String.format("%d.bmp", uri.hashCode()), Context.MODE_PRIVATE));
		} catch(IOException ex) {
			Log.e(TAG, String.format("error caching %s", uri));
		}
	}
	
	private static Bitmap cachedAvatar(Context context, URI uri) {
		try {
			if(uri != null) {
				return BitmapFactory.decodeStream(
					context.openFileInput(String.format("%d.bmp", uri.hashCode())));
			}
		} catch(IOException ex) {
			Log.e(TAG, String.format("error reading cached %s", uri));
		}
		return null;
	}
	
	private static FlairInfo parseFlair(Context context, URI uri, String src) throws JSONException {
		
		JSONObject json = new JSONObject(src);
		FlairInfo flair = new FlairInfo();
		
		JSONObject user = json.getJSONArray("items").getJSONObject(0);
		
		flair.setUserId(user.getLong("user_id"));
		
		try {
			flair.setAvatarURI(new URI(user.getString("profile_image")));
			
			flair.setProfileUrl(URI.create(String.format(
				context.getString(org.ocactus.soflair.R.string.recent_uri),
				uri.getHost().replace("api.", ""), flair.getUserId())));
			
		} catch(URISyntaxException ex) {
			Log.w(TAG, "invalid uri (" + ex.getMessage() + ")");
		}
		
		flair.setDisplayName(user.getString("display_name"));
		flair.setReputation(user.getLong("reputation"));
		
		JSONObject badges = user.getJSONObject("badge_counts");
		flair.setNumberOfGoldBadges(badges.getInt("gold"));
		flair.setNumberOfSilverBadges(badges.getInt("silver"));
		flair.setNumberOfBronzeBadges(badges.getInt("bronze"));
		
		return flair;
	}
	
	private static int calcAvatarSize(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return (int) (60f * displayMetrics.density + 0.5f);
	}
	
	private static String downloadJson(URI uri) {
		try {
			Log.i(TAG, "downloading user flair from " + uri);
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(uri);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(httpResponse.getEntity().getContent())), 8 * 1024);
			
			StringBuffer response = new StringBuffer();
			String line;
			while((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			return response.toString();
			
		} catch(IOException ex) {
			Log.e(TAG, String.format("error downloading flair (%s)", uri));
			return null;
		}
	}
	
	private static Bitmap downloadBitmap(URI uri) {
		try {
			URLConnection connection = uri.toURL().openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, 8 * 1024);
            Bitmap bmp = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close(); 
            return bmp;
		} catch(Exception ex) {
			Log.w(TAG, "couldn't download image (" + ex.getMessage() + ")");
			return null;
		}
	}
}