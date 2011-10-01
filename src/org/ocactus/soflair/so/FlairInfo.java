package org.ocactus.soflair.so;

import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;

import android.graphics.Bitmap;

public class FlairInfo {
	
	private long userId;
	private URI profileUrl;
	private String displayName;
	private long reputation;
	private int numberOfGoldBadges;
	private int numberOfSilverBadges;
	private int numberOfBronzeBadges;
	private Bitmap avatar;
	private URI avatarURI;
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public Bitmap getAvatar() {
		return avatar;
	}

	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}
	
	protected URI getAvatarURI() {
		return avatarURI;
	}
	
	protected void setAvatarURI(URI avatarURI) {
		this.avatarURI = avatarURI;
	}

	public URI getProfileUrl() {
		return profileUrl;
	}
	
	public void setProfileUrl(URI profileUrl) {
		this.profileUrl = profileUrl;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public long getReputation() {
		return reputation;
	}
	
	public void setReputation(long reputation) {
		this.reputation = reputation;
	}
	
	public int getNumberOfGoldBadges() {
		return numberOfGoldBadges;
	}

	public void setNumberOfGoldBadges(int numberOfGoldBadges) {
		this.numberOfGoldBadges = numberOfGoldBadges;
	}

	public int getNumberOfSilverBadges() {
		return numberOfSilverBadges;
	}

	public void setNumberOfSilverBadges(int numberOfSilverBadges) {
		this.numberOfSilverBadges = numberOfSilverBadges;
	}

	public int getNumberOfBronzeBadges() {
		return numberOfBronzeBadges;
	}

	public void setNumberOfBronzeBadges(int numberOfBronzeBadges) {
		this.numberOfBronzeBadges = numberOfBronzeBadges;
	}

	public String getReputationDisplayString() {
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		numberFormat.setMaximumFractionDigits(1);
		if(reputation < 10000) {
			return numberFormat.format(reputation);
		} else {
			return numberFormat.format(reputation / 1000.0) + "k";
		}
	}
}