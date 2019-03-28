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

	// TODO! return Netty HttpResponse instead - Configuration shouldn't embedded
	// here? - can remove configuration param and get via service provider
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

		// request.getParams();
		// con.getOutputStream();

		// Used to set authentication in headers for OAuth2
		if (initConnection != null) {
			initConnection.accept(con);
		}

		if (request.hasParamsInBody()) {
			con.setDoOutput(true);

			if (!request.getParams().isEmpty()) {
				con.setRequestProperty("content-type", "application/json");
				String requestBody = buildRequestBody(request, jsonConfiguration);
				System.err.println("request body: " + requestBody);
				// TODO! encoding
				byte[] requestBodyBytes = requestBody.getBytes();
				con.setFixedLengthStreamingMode(requestBodyBytes.length);
				con.getOutputStream().write(requestBodyBytes);
			} else {
				// Prevent 411 Content Length Required
				con.setFixedLengthStreamingMode(0);
			}
		}

		boolean isOK;
		String responseBody;
		try {
			isOK = con.getResponseCode() == HTTP_OK;
			InputStream responseStream = isOK ? con.getInputStream() : con.getErrorStream();
			// con.getHeaderField(n);
			// 401 con.getInputStream() throws error; con.getErrorStream() returns null
			responseBody = responseStream == null ? "" : IOUtil.readStringAndClose(responseStream);
		} finally {
			con.disconnect();
		}

		// TODO! what to do when !isOK
		if (!isOK) {
			// TODO! logger?
			throw new IllegalStateException(url + " response was " + con.getResponseCode() + " "
					+ con.getResponseMessage() + " " + responseBody);
		}

		// TODO! Very long Json does not get displayed in the console
		System.err.println(responseBody);

		// TODO! wrap/convert response json
		// if ("json".equals(request.getParam("format"))) {
		if (responseBody.startsWith("{")) {
			OAuthJsonResponse response = new OAuthJsonResponse(responseBody, jsonConfiguration);
			// TODO! change to !"pass"
			// TODO! pass/fail specific to Flickr?
//			if ("fail".equals(response.getString("stat"))) {
//				throw new IllegalStateException(body);
//			}
			return response;
		} else {
			// TODO! Google request hitting this...
			return new OAuthAuthResponse(responseBody);
		}
	}

	private String buildRequestBody(OAuthRequest request, Configuration jsonConfiguration) {
		return jsonConfiguration.jsonProvider().toJson(request.getParams());
	}

	abstract protected String getUrl(OAuthRequest request);

	// make this private?
	protected final String getQueryString(Map<String, Object> params, Function<String, String> valueEncoder) {
		StringBuilder queryStringBuilder = new StringBuilder();

		boolean isFirstParam = true;
		for (Entry<String, Object> paramEntry : params.entrySet()) {
			if (isFirstParam) {
				isFirstParam = false;
			} else {
				queryStringBuilder.append("&");
			}
			queryStringBuilder.append(paramEntry.getKey());
			queryStringBuilder.append("=");
			queryStringBuilder.append(valueEncoder.apply(paramEntry.getValue().toString()));
		}

		return queryStringBuilder.toString();
	}
}
