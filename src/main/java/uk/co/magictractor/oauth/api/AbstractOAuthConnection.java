package uk.co.magictractor.oauth.api;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import com.jayway.jsonpath.Configuration;

import uk.co.magictractor.oauth.util.IOUtil;

// Common code for OAuth1 and OAuth2 implementations.
public abstract class AbstractOAuthConnection implements OAuthConnection {

	// TODO! return Netty HttpResponse instead - Configuration shouldn't embedded here?
	protected OAuthResponse request0(OAuthRequest request, Configuration jsonConfiguration) throws IOException {
		return request0(request, jsonConfiguration, null);
	}

	// http://www.baeldung.com/java-http-request
	protected OAuthResponse request0(OAuthRequest request, Configuration jsonConfiguration,
			Consumer<HttpURLConnection> initConnection) throws IOException {
		// To look at URLStreamHandler
		URL url = new URL(getUrl(request));
		// TODO! add query string...
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(request.getHttpMethod());

		// Used to set authentication in headers for OAuth2
		if (initConnection != null) {
			initConnection.accept(con);
		}

		// Prevent 411 Content Length Required
		if ("POST".equals(request.getHttpMethod())) {
			// con.setRequestProperty("Content-Length", "0");
			con.setDoOutput(true);
			con.setFixedLengthStreamingMode(0);
		}

		boolean isOK;
		String body;
		try {
			isOK = con.getResponseCode() == HTTP_OK;
			InputStream responseStream = isOK ? con.getInputStream() : con.getErrorStream();
			//con.getHeaderField(n);
			// 401 con.getInputStream() throws error; con.getErrorStream() returns null
			body = responseStream == null ? "" : IOUtil.readStringAndClose(responseStream);
		} finally {
			con.disconnect();
		}

		// TODO! what to do when !isOK
		if (!isOK) {
			// TODO! logger?
			throw new IllegalStateException(
					url + " response was " + con.getResponseCode() + " " + con.getResponseMessage() + " " + body);
		}

		// TODO! Very long Json does not get displayed in the console
		System.err.println(body);

		// TODO! wrap/convert response json
		// if ("json".equals(request.getParam("format"))) {
		if (body.startsWith("{")) {
			OAuthJsonResponse response = new OAuthJsonResponse(body, jsonConfiguration);
			// TODO! change to !"pass"
			// TODO! pass/fail specific to Flickr?
//			if ("fail".equals(response.getString("stat"))) {
//				throw new IllegalStateException(body);
//			}
			return response;
		} else {
			// TODO! Google request hitting this...
			return new OAuthAuthResponse(body);
		}
	}

	abstract protected String getUrl(OAuthRequest request);

	// make this private?
	protected final String getQueryString(Map<String, String> params, Function<String, String> valueEncoder) {
		StringBuilder queryStringBuilder = new StringBuilder();
		// urlBuilder.append("?");

		boolean isFirstParam = true;
		for (Entry<String, String> paramEntry : params.entrySet()) {
			if (isFirstParam) {
				isFirstParam = false;
			} else {
				queryStringBuilder.append("&");
			}
			queryStringBuilder.append(paramEntry.getKey());
			queryStringBuilder.append("=");
			queryStringBuilder.append(valueEncoder.apply(paramEntry.getValue()));
			// urlBuilder.append("&");
		}

		return queryStringBuilder.toString();
	}
}
