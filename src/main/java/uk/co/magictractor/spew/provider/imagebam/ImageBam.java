package uk.co.magictractor.spew.provider.imagebam;

import java.util.function.Supplier;

import uk.co.magictractor.spew.api.BadResponseException;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.verification.AuthorizationHandler;
import uk.co.magictractor.spew.core.verification.PasteVerificationCodeHandler;
import uk.co.magictractor.spew.core.verification.VerificationFunction;
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
    public String getContentType(SpewHttpResponse response) {
        // Workaround for header reporting "text/hmtl" for JSON
        String contentType = ContentTypeUtil.fromHeader(response);
        if ("text/html".contentEquals(contentType) && HttpMessageUtil.bodyStartsWith(response, "{")) {
            contentType = ContentTypeUtil.JSON_MIME_TYPES.get(0);
        }

        return contentType;
    }

    @Override
    public String getTemporaryCredentialRequestUri() {
        return "http://www.imagebam.com/sys/oauth/request_token";
    }

    @Override
    public String getResourceOwnerAuthorizationUri() {
        return "http://www.imagebam.com/sys/oauth/authorize_token";
    }

    @Override
    public String getTokenRequestUri() {
        return "http://www.imagebam.com/sys/oauth/request_access_token";
    }

    @Override
    public String getRequestSignatureMethod() {
        return "MD5";
    }

    // {"rsp":{"status":"fail","error_code":108,"error_message":"permission denied: gallery_id"}}
    @Override
    public void verifyResponse(SpewParsedResponse response) {
        String status = response.getString("$.rsp.status");
        if (!"ok".equals(status)) {
            String errorCode = response.getString("$.rsp.error_code");
            String errorMessage = response.getString("$.rsp.error_message");
            throw new BadResponseException(status, errorCode, errorMessage);
        }
    }

    @Override
    public AuthorizationHandler getDefaultAuthorizationHandler(
            Supplier<VerificationFunction> verificationFunctionSupplier) {
        // ImageBam does not do callbacks, it always displays a code to copy into the application.
        return new PasteVerificationCodeHandler(verificationFunctionSupplier);
    }

    // Cannot edit application details, only add client.
    @Override
    public String appManagementUrl() {
        return "http://www.imagebam.com/sys/API/clients";
    }

}
