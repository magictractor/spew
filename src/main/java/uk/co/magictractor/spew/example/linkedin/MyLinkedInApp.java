package uk.co.magictractor.spew.example.linkedin;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewApplicationCache;
import uk.co.magictractor.spew.api.SpewOAuth2Application;
import uk.co.magictractor.spew.provider.linkedin.LinkedIn;

public class MyLinkedInApp implements SpewOAuth2Application<LinkedIn> {

    private static final MyLinkedInApp INSTANCE = SpewApplicationCache.add(MyLinkedInApp.class);

    public static MyLinkedInApp get() {
        return INSTANCE;
    }

    private MyLinkedInApp() {
    }

    // TODO! granted scope should be in the store to allow re-authorization if the scope changes
    @Override
    public String getScope() {
        // https://docs.microsoft.com/en-us/linkedin/consumer/integrations/self-serve/share-on-linkedin
        // https://docs.microsoft.com/en-us/linkedin/marketing/integrations/community-management/shares/network-update-social-actions
        // r_member_social is not authorized for your application
        return "r_member_social,w_member_social";
    }

    @Override
    public String toString() {
        return SpewApplication.toStringHelper(this).toString();
    }

}
