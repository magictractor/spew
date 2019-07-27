/**
 * Copyright 2015-2019 Ken Dobson
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
package uk.co.magictractor.spew.core.response.parser.jayway;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.core.response.parser.ObjectCentricSpewParsedResponse;
import uk.co.magictractor.spew.util.IOUtil;

/**
 *
 */
public class JaywayResponse implements ObjectCentricSpewParsedResponse {

    private ReadContext ctx;

    /**
     * Default visibility because instances should only be created via
     * JaywayResponseParserInit.
     */
    /* default */ JaywayResponse(SpewApplication application, SpewResponse response) {
        IOUtil.consumeThenClose(response.getBodyInputStream(), body -> {
            ctx = JsonPath.parse(body, application.getServiceProvider().getJsonConfiguration());
        });
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        return ctx.read(key, type);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> elementType) {
        return ctx.read(key, new TypeRef<List<T>>() {
            @Override
            public Type getType() {
                return new ParameterizedType() {

                    @Override
                    public Type getRawType() {
                        return List.class;
                    }

                    @Override
                    public Type getOwnerType() {
                        return null;
                    }

                    @Override
                    public Type[] getActualTypeArguments() {
                        return new Type[] { elementType };
                    }
                };
            }
        });
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ctx.json", ctx.json())
                .toString();
    }

}
