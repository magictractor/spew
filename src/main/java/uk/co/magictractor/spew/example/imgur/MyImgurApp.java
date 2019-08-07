package uk.co.magictractor.spew.example.imgur;

import java.util.function.Supplier;

import uk.co.magictractor.spew.access.AuthorizationHandler;
import uk.co.magictractor.spew.api.OAuth2Application;
import uk.co.magictractor.spew.api.OAuth2ServiceProvider;
import uk.co.magictractor.spew.api.VerificationFunction;
import uk.co.magictractor.spew.provider.imgur.Imgur;
import uk.co.magictractor.spew.server.LocalServerAuthorizationHandler;

public class MyImgurApp implements OAuth2Application {

    @Override
    public OAuth2ServiceProvider getServiceProvider() {
        return Imgur.getInstance();
    }

    @Override
    public AuthorizationHandler getAuthorizationHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        //return new PasteVerificationCodeHandler(verificationFunction);
        return new LocalServerAuthorizationHandler(verificationFunctionSupplier);
    }

}
