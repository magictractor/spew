package uk.co.magictractor.spew.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;

// https://stackoverflow.com/questions/34111276/jsonpath-with-jackson-or-gson
// https://github.com/json-path/JsonPath
public class SpewJaywayResponse implements SpewResponse {

    private ReadContext ctx;

    public SpewJaywayResponse(String response, Configuration jsonConfiguration) {
        ctx = JsonPath.parse(response, jsonConfiguration);
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
