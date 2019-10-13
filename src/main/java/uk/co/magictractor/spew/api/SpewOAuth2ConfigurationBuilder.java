/**
 * Copyright 2019 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.spew.api;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public class SpewOAuth2ConfigurationBuilder {

    private SpewOAuth2ConfigurationImpl configuration = new SpewOAuth2ConfigurationImpl();
    private boolean done;

    public SpewOAuth2Configuration build() {
        done = true;

        return configuration;
    }

    public Supplier<SpewOAuth2Configuration> nextBuild() {
        return () -> {
            if (!done) {
                throw new IllegalStateException("Not built yet");
            }
            return configuration;
        };
    }

    public SpewOAuth2ConfigurationBuilder withServiceProvider(SpewOAuth2ServiceProvider serviceProvider) {
        //        if (configuration.temporaryCredentialRequestUri == null) {
        //            configuration.temporaryCredentialRequestUri = serviceProvider.getTemporaryCredentialRequestUri();
        //        }
        //        if (configuration.resourceOwnerAuthorizationUri == null) {
        //            configuration.resourceOwnerAuthorizationUri = serviceProvider.getResourceOwnerAuthorizationUri();
        //        }
        //        if (configuration.tokenRequestUri == null) {
        //            configuration.tokenRequestUri = serviceProvider.getTokenRequestUri();
        //        }
        //        if (configuration.requestSignatureMethod == null) {
        //            configuration.requestSignatureMethod = serviceProvider.getRequestSignatureMethod();
        //        }
        //        if (configuration.signatureFunction == null) {
        //            configuration.signatureFunction = SPIUtil.firstNotNull(SignatureGenerator.class,
        //                gen -> gen.signatureFunction(configuration.requestSignatureMethod))
        //                    .orElse(null);
        //        }

        return this;
    }

    public SpewOAuth2ConfigurationBuilder withApplication(SpewOAuth2Application<?> application) {
        //        if (configuration.consumerKey == null) {
        //            configuration.consumerKey = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
        //                    .getProperty(application, "consumer_key");
        //        }
        //        if (configuration.consumerSecret == null) {
        //            configuration.consumerSecret = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
        //                    .getProperty(application, "consumer_secret");
        //        }
        //        if (configuration.userTokenProperty == null) {
        //            configuration.userTokenProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
        //                    .getProperty(application, "user_token");
        //        }
        //        if (configuration.userSecretProperty == null) {
        //            configuration.userSecretProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
        //                    .getProperty(application, "user_secret");
        //        }

        withProperties(application.getProperties());

        return this;
    }

    public SpewOAuth2ConfigurationBuilder withProperties(Map<String, Object> properties) {
        configuration.properties.putAll(properties);
        return this;
    }

    // move this??
    // See https://www.flickr.com/services/api/auth.oauth.html
    private static String getSignatureBaseString(OutgoingHttpRequest request) {
        StringBuilder signatureBaseStringBuilder = new StringBuilder();
        signatureBaseStringBuilder.append(request.getHttpMethod());
        signatureBaseStringBuilder.append('&');
        signatureBaseStringBuilder.append(oauthEncode(request.getPath()));
        signatureBaseStringBuilder.append('&');
        signatureBaseStringBuilder.append(oauthEncode(getSignatureQueryString(request)));

        return signatureBaseStringBuilder.toString();
    }

    private static String getSignatureQueryString(OutgoingHttpRequest request) {
        // TODO! maybe ignore some params - see Flickr upload photo
        TreeMap<String, Object> orderedParams = new TreeMap<>(request.getQueryStringParams());
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : orderedParams.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                stringBuilder.append('&');
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append('=');
            stringBuilder.append(oauthEncode(entry.getValue().toString()));
        }

        return stringBuilder.toString();
    }

    // https://stackoverflow.com/questions/5864954/java-and-rfc-3986-uri-encoding
    private static final String oauthEncode(String string) {
        // TODO! something more efficient?
        String urlEncoded = ExceptionUtil.call(() -> URLEncoder.encode(string, "UTF-8"));
        return urlEncoded.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private static final class SpewOAuth2ConfigurationImpl implements SpewOAuth2Configuration {

        private Map<String, Object> properties = new LinkedHashMap<>();

        @Override
        public void addProperties(Map<String, Object> properties) {
            properties.putAll(this.properties);
        }

    }

}
