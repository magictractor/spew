package uk.co.magictractor.spew.api.boa;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jayway.jsonpath.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.AbstractConnection;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewJaywayResponse;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.SpewServiceProvider;
import uk.co.magictractor.spew.connection.ConnectionRequest;
import uk.co.magictractor.spew.connection.ConnectionRequestFactory;
import uk.co.magictractor.spew.imagebam.ImageBam;
import uk.co.magictractor.spew.util.IOUtil;

// Common code for OAuth1 and OAuth2 implementations.
public abstract class AbstractBoaOAuthConnection<APP extends SpewApplication, SP extends SpewServiceProvider>
        extends AbstractConnection<APP, SP> implements SpewConnection {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractBoaOAuthConnection(APP application) {
        super(application);
    }

    protected final Logger getLogger() {
        return logger;
    }

    // TODO! return Netty HttpResponse instead - Configuration shouldn't embedded
    // here? - can remove configuration param and get via service provider
    protected SpewResponse request0(SpewRequest request, Configuration jsonConfiguration) throws IOException {
        return request0(request, jsonConfiguration, null);
    }

    // http://www.baeldung.com/java-http-request
    protected SpewResponse request0(SpewRequest request, Configuration jsonConfiguration,
            Consumer<HttpURLConnection> initConnection) throws IOException {

        URL url = new URL(getUrl(request));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(request.getHttpMethod());

        // request.getParams();
        // con.getOutputStream();

        // Used to set authentication in headers for OAuth2
        if (initConnection != null) {
            initConnection.accept(con);
        }

        //        if (request.hasParamsInBody()) {
        //            // moves this too?
        //            con.setDoOutput(true);
        //
        //            if (!request.getParams().isEmpty()) {
        //                con.setRequestProperty("content-type", "application/json");
        //                String requestBody = buildRequestBody(request, jsonConfiguration);
        //                logger.trace("request body: " + requestBody);
        //                // TODO! encoding
        //                byte[] requestBodyBytes = requestBody.getBytes();
        //                con.setFixedLengthStreamingMode(requestBodyBytes.length);
        //                con.getOutputStream().write(requestBodyBytes);
        //            }
        //            else {
        //                // Prevent 411 Content Length Required
        //                con.setFixedLengthStreamingMode(0);
        //            }
        //
        //           ConnectionRequest connectionRequest = ConnectionRequestFactory.createConnectionRequest(request);
        //           connectionRequest.setRequestBody(request, jsonConfiguration);
        //
        //        }

        ConnectionRequest connectionRequest = ConnectionRequestFactory.createConnectionRequest(con);
        connectionRequest.writeParams(request, jsonConfiguration);

        boolean isOK;
        String responseBody;
        try {
            isOK = con.getResponseCode() == HTTP_OK;
            InputStream responseStream = isOK ? con.getInputStream() : con.getErrorStream();
            // con.getHeaderField(n);
            // 401 con.getInputStream() throws error; con.getErrorStream() returns null
            responseBody = responseStream == null ? "" : IOUtil.readStringAndClose(responseStream);
        }
        finally {
            con.disconnect();
        }

        // TODO! what to do when !isOK - now have BadResponseException
        if (!isOK) {
            // TODO! logger?
            throw new IllegalStateException(url + " response was " + con.getResponseCode() + " "
                    + con.getResponseMessage() + " " + responseBody);
        }

        // TODO! Very long Json does not get displayed in the console
        logger.trace(responseBody);

        SpewResponse response;

        // TODO! wrap/convert response json
        // if ("json".equals(request.getParam("format"))) {
        // TODO! check header for content type
        // TODO! make case insensitive?
        String contentType = getHeader(con, "content-type");
        if ("application/json".equals(contentType) || contentType.startsWith("application/json;")) {
            //        if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
            response = new SpewJaywayResponse(responseBody, jsonConfiguration);
            // TODO! change to !"pass"
            // TODO! pass/fail specific to Flickr?
            //			if ("fail".equals(response.getString("stat"))) {
            //				throw new IllegalStateException(body);
            //			}
        }
        // TODO! Imagebam "text/html; charset=UTF-8"
        else if (getServiceProvider() instanceof ImageBam) {
            response = new SpewJaywayResponse(responseBody, jsonConfiguration);
        }
        else {
            throw new IllegalStateException(
                "code needs modified to handle Content-Type " + contentType + " " + responseBody);
        }

        getServiceProvider().verifyResponse(response);

        return response;
    }

    private String getHeader(HttpURLConnection con, String headerKey) {
        //List<String> headerValues = con.getHeaderFields().get(headerKey);

        String lowerCaseHeaderKey = headerKey.toLowerCase();
        List<String> headerValues = con.getHeaderFields()
                .entrySet()
                .stream()
                .filter(e -> e.getKey() != null && e.getKey().toLowerCase().equals(lowerCaseHeaderKey))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (headerValues == null) {
            return null;
        }
        if (headerValues.size() > 1) {
            throw new IllegalStateException();
        }
        return headerValues.get(0);
    }

    //    private String buildRequestBody(SpewRequest request, Configuration jsonConfiguration) {
    //        return jsonConfiguration.jsonProvider().toJson(request.getParams());
    //    }

    abstract protected String getUrl(SpewRequest request);

}
