package uk.co.magictractor.spew.provider.imagebam;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.io.BaseEncoding;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewOAuth1Configuration;
import uk.co.magictractor.spew.api.SpewOAuth1ConfigurationBuilder;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseBuilder;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;
import uk.co.magictractor.spew.core.verification.AuthVerificationHandler;
import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;

/**
 * <p>
 * Meh, Imagebam doesn't look promising. API is patchy - looks like we can't
 * list all images (only list those in a given gallery), or move images to and
 * from a gallery.
 * </p>
 * <p>
 * It also handles passwords badly - it will email password reminders in plain
 * text.
 * </p>
 * <p>
 * ImageBam will not be used by any of my apps, but is retained here because it
 * presents some interested challenges to OAuth clients.
 * </p>
 */
public class ImageBam implements SpewOAuth1ServiceProvider {

    public static final String REST_ENDPOINT = "http://www.imagebam.com/sys/API/resource/";

    private ImageBam() {
    }

    @Override
    public void initConnectionConfigurationBuilder(SpewOAuth1ConfigurationBuilder builder) {
        Supplier<SpewOAuth1Configuration> configurationSupplier = builder.nextBuild();
        builder.withRequestSignatureMethod("MD5");
        builder.withSignatureBaseStringFunction(req -> getImageBamSignatureBaseString(req, configurationSupplier));
        builder.withSignatureEncodingFunction(BaseEncoding.base16().lowerCase());

        // Imagebam only supports copy and paste of a code.
        builder.withoutVerificationHandlerType(AuthVerificationHandler.AUTH_VERIFICATION_TYPE_CALLBACK);
    }

    private String getImageBamSignatureBaseString(OutgoingHttpRequest request,
            Supplier<SpewOAuth1Configuration> configurationSupplier) {
        SpewOAuth1Configuration configuration = configurationSupplier.get();

        StringBuilder signatureBaseStringBuilder = new StringBuilder();

        signatureBaseStringBuilder.append(configuration.getConsumerKey());
        signatureBaseStringBuilder.append(configuration.getConsumerSecret());
        signatureBaseStringBuilder.append(request.getQueryStringParam("oauth_timestamp").get());
        signatureBaseStringBuilder.append(request.getQueryStringParam("oauth_nonce").get());
        if (configuration.getUserTokenProperty().getValue() != null) {
            // aaah... token and secret were swapped out... => expires idea is better??
            signatureBaseStringBuilder.append(configuration.getUserTokenProperty().getValue());
            signatureBaseStringBuilder.append(configuration.getUserSecretProperty().getValue());
        }

        return signatureBaseStringBuilder.toString();
    }

    @Override
    public void buildParsedResponse(SpewParsedResponseBuilder parsedResponseBuilder) {

        // Workaround for header containing "text/hmtl" for Json payloads
        String contentType = parsedResponseBuilder.getContentType();
        if ("text/html".contentEquals(contentType) && HttpMessageUtil.bodyStartsWith(parsedResponseBuilder, "{")) {
            parsedResponseBuilder.withContentType(ContentTypeUtil.JSON_MIME_TYPES.get(0));
        }
    }

    @Override
    public String oauth1TemporaryCredentialRequestUri() {
        return "http://www.imagebam.com/sys/oauth/request_token";
    }

    @Override
    public String oauth1ResourceOwnerAuthorizationUri() {
        return "http://www.imagebam.com/sys/oauth/authorize_token";
    }

    @Override
    public String oauth1TokenRequestUri() {
        return "http://www.imagebam.com/sys/oauth/request_access_token";
    }

    // {"rsp":{"status":"fail","error_code":108,"error_message":"permission denied: gallery_id"}}
    // TODO! move to new build method
    //    @Override
    //    public void verifyResponse(SpewHttpMessageBodyReader response) {
    //        String status = response.getBodyString("$.rsp.status");
    //        if (!"ok".equals(status)) {
    //            String errorCode = response.getBodyString("$.rsp.error_code");
    //            String errorMessage = response.getBodyString("$.rsp.error_message");
    //            throw new BadResponseException(status, errorCode, errorMessage);
    //        }
    //    }

    // Cannot edit application details, only add client.
    @Override
    public String appManagementUrl() {
        return "http://www.imagebam.com/sys/API/clients";
    }

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Collections.emptyList();
    }

}
// {"rsp":{"status":"ok","galleries":[]}}
// Incorrect URL gives an HTML 404 with HTML body
// Invalid token gives form data IncomingApacheHttpClientResponse{status=401, body=oauth_problem=token_rejected}
