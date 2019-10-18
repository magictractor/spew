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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.io.BaseEncoding;

import uk.co.magictractor.spew.core.signature.SignatureGenerator;
import uk.co.magictractor.spew.store.EditableProperty;
import uk.co.magictractor.spew.store.application.ApplicationPropertyStore;
import uk.co.magictractor.spew.store.user.UserPropertyStore;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class SpewOAuth1ConfigurationBuilder {

    private SpewOAuth1ConfigurationImpl configuration = new SpewOAuth1ConfigurationImpl();
    private boolean done;

    public SpewOAuth1Configuration build() {
        if (configuration.signatureBaseStringFunction == null) {
            withSignatureBaseStringFunction(SpewOAuth1ConfigurationBuilder::getSignatureBaseString);
        }
        if (configuration.signatureEncodingFunction == null) {
            withSignatureEncodingFunction(BaseEncoding.base64());
        }

        done = true;

        return configuration;
    }

    public Supplier<SpewOAuth1Configuration> nextBuild() {
        return () -> {
            if (!done) {
                throw new IllegalStateException("Not built yet");
            }
            return configuration;
        };
    }

    public SpewOAuth1ConfigurationBuilder withSignatureBaseStringFunction(
            Function<OutgoingHttpRequest, String> signatureBaseStringFunction) {
        configuration.signatureBaseStringFunction = signatureBaseStringFunction;
        return this;
    }

    public SpewOAuth1ConfigurationBuilder withSignatureEncodingFunction(
            Function<byte[], String> signatureEncodingFunction) {
        configuration.signatureEncodingFunction = signatureEncodingFunction;
        return this;
    }

    public SpewOAuth1ConfigurationBuilder withSignatureEncodingFunction(BaseEncoding encoding) {
        return withSignatureEncodingFunction(bytes -> encoding.encode(bytes));
    }

    public SpewOAuth1ConfigurationBuilder withServiceProvider(SpewOAuth1ServiceProvider serviceProvider) {
        if (configuration.temporaryCredentialRequestUri == null) {
            configuration.temporaryCredentialRequestUri = serviceProvider.oauth1TemporaryCredentialRequestUri();
        }
        if (configuration.resourceOwnerAuthorizationUri == null) {
            configuration.resourceOwnerAuthorizationUri = serviceProvider.oauth1ResourceOwnerAuthorizationUri();
        }
        if (configuration.tokenRequestUri == null) {
            configuration.tokenRequestUri = serviceProvider.oauth1TokenRequestUri();
        }
        if (configuration.requestSignatureMethod == null) {
            configuration.requestSignatureMethod = serviceProvider.oauth1RequestSignatureMethod();
        }
        if (configuration.signatureFunction == null) {
            configuration.signatureFunction = SPIUtil.firstNotNull(SignatureGenerator.class,
                gen -> gen.signatureFunction(configuration.requestSignatureMethod))
                    .orElse(null);
        }

        return this;
    }

    public SpewOAuth1ConfigurationBuilder withApplication(SpewOAuth1Application<?> application) {
        if (configuration.consumerKey == null) {
            configuration.consumerKey = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "consumer_key");
        }
        if (configuration.consumerSecret == null) {
            configuration.consumerSecret = SPIUtil.firstAvailable(ApplicationPropertyStore.class)
                    .getProperty(application, "consumer_secret");
        }
        if (configuration.userTokenProperty == null) {
            configuration.userTokenProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
                    .getProperty(application, "user_token");
        }
        if (configuration.userSecretProperty == null) {
            configuration.userSecretProperty = SPIUtil.firstAvailable(UserPropertyStore.class)
                    .getProperty(application, "user_secret");
        }
        withProperties(application.getProperties());

        return this;
    }

    public SpewOAuth1ConfigurationBuilder withProperties(Map<String, Object> properties) {
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

    private static final class SpewOAuth1ConfigurationImpl implements SpewOAuth1Configuration {

        private String consumerKey;
        private String consumerSecret;
        private EditableProperty userTokenProperty;
        private EditableProperty userSecretProperty;
        private String temporaryCredentialRequestUri;
        private String resourceOwnerAuthorizationUri;
        private String tokenRequestUri;
        private String requestSignatureMethod;
        private Function<OutgoingHttpRequest, String> signatureBaseStringFunction;
        private BiFunction<byte[], byte[], byte[]> signatureFunction;
        private Function<byte[], String> signatureEncodingFunction;
        private Map<String, Object> properties = new LinkedHashMap<>();

        @Override
        public String getConsumerKey() {
            return consumerKey;
        }

        @Override
        public String getConsumerSecret() {
            return consumerSecret;
        }

        @Override
        public EditableProperty getUserTokenProperty() {
            return userTokenProperty;
        }

        @Override
        public EditableProperty getUserSecretProperty() {
            return userSecretProperty;
        }

        @Override
        public String getTemporaryCredentialRequestUri() {
            return temporaryCredentialRequestUri;
        }

        @Override
        public String getResourceOwnerAuthorizationUri() {
            return resourceOwnerAuthorizationUri;
        }

        @Override
        public String getTokenRequestUri() {
            return tokenRequestUri;
        }

        @Override
        public String getRequestSignatureMethod() {
            return requestSignatureMethod;
        }

        @Override
        public Function<OutgoingHttpRequest, String> getSignatureBaseStringFunction() {
            return signatureBaseStringFunction;
        }

        @Override
        public BiFunction<byte[], byte[], byte[]> getSignatureFunction() {
            return signatureFunction;
        }

        @Override
        public Function<byte[], String> getSignatureEncodingFunction() {
            return signatureEncodingFunction;
        }

        @Override
        public void addProperties(Map<String, Object> properties) {
            properties.putAll(this.properties);
        }

    }

}
