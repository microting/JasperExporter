package com.microting.report.jasper;

import lombok.experimental.UtilityClass;

import java.net.HttpURLConnection;
import java.net.URL;

@UtilityClass
public class ImageUtils {

	@SuppressWarnings("unused")
	public static boolean isValidImage(String url) {
		if (isBlank(url)) {
			return false;
		}

		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("HEAD");
			return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception ignored) {
			return false;
		}
	}

	private static boolean isBlank(String string) {
		return string == null || string.trim().isEmpty();
	}
}