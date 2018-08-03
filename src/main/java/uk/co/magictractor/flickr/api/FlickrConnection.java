package uk.co.magictractor.flickr.api;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.magictractor.flickr.util.ExceptionUtil;

public class FlickrConnection {

	public static FlickrResponse request(FlickrRequest request) {
		return ExceptionUtil.call(() -> request0(request));
	}

	// http://www.baeldung.com/java-http-request
	private static FlickrResponse request0(FlickrRequest request) throws IOException {
		URL url = new URL(request.getUrl());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(request.getHttpMethod());

		boolean isOK;
		StringBuilder bodyBuilder = new StringBuilder();
		BufferedReader in = null;
		try {
			isOK = con.getResponseCode() == HTTP_OK;
			InputStream responseStream = isOK ? con.getInputStream() : con.getErrorStream();

			// TODO! encoding
			in = new BufferedReader(new InputStreamReader(responseStream));
			char[] buffer = new char[1024];
			int len;
			while ((len = in.read(buffer)) != -1) {
				bodyBuilder.append(buffer, 0, len);
			}

//			String line;
//			while ((line = in.readLine()) != null) {
//				System.err.println(line);
//			}
		} finally {
			if (in != null) {
				in.close();
			}
			con.disconnect();
		}

		// TODO! what to do when !isOK
		if (!isOK) {
			// TODO! logger?
			throw new IllegalStateException(con.getResponseCode() + " " + con.getResponseMessage() + " " + bodyBuilder.toString());
		}

		// TODO! Very long Json does not get displayed in the console
		System.err.println(bodyBuilder.toString());

		// TODO! wrap/convert response json
		if ("json".equals(request.getParam("format"))) {
			return new FlickrJsonResponse(bodyBuilder.toString());
		} else {
			return new FlickrAuthResponse(bodyBuilder.toString());
		}
	}

}
