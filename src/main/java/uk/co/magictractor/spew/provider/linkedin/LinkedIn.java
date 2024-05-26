package uk.co.magictractor.spew.provider.linkedin;

import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;

// https://docs.microsoft.com/en-us/linkedin/shared/authentication/authorization-code-flow
// https://docs.microsoft.com/en-us/linkedin/shared/api-guide/concepts/methods
// https://docs.microsoft.com/en-us/linkedin/shared/api-guide/concepts/pagination
// https://docs.microsoft.com/en-us/linkedin/shared/integrations/people/connections-api
public class LinkedIn implements SpewOAuth2ServiceProvider {

    private LinkedIn() {
    }

    @Override
    public String oauth2AuthorizationUri() {
        return "https://www.linkedin.com/oauth/v2/authorization";
    }

    @Override
    public String oauth2TokenUri() {
        return "https://www.linkedin.com/oauth/v2/accessToken";
    }

    /**
     * <p>
     * A very restricted set of scopes is available to an application by
     * default. Additional scopes require joining partner programs and LinkedIn
     * says
     * </p>
     * 
     * <pre>
     * We only grant access for marketing related use cases - all
     * other requests for access will be rejected</quote>.
     * </pre>
     * <p>
     * Default scopes allow posting articles and URLs, but does not permit
     * reading existing articles and URLs.
     * </p>
     *
     * @see https://stackoverflow.com/questions/54265367/how-can-i-change-a-linkedin-apps-permissions
     */
    @Override
    public String appManagementUrl() {
        return "https://www.linkedin.com/developers/apps";
    }

    // TODO! stop these being added to auth calls
    @Override
    public void prepareRequest(ApplicationRequest request) {
        //request.setQueryStringParam("format", "json");
        //request.setQueryStringParam("nojsoncallback", "1");
    }

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Arrays.asList(
        //BooleanTypeAdapter.ZERO_AND_ONE,
        //new LocalDateTimeTypeAdapter("yyyy-MM-dd HH:mm:ss"),
        //InstantEpochTypeAdapter.SECONDS,
        //TagSetTypeAdapter.getInstance()
        );
    }

}
