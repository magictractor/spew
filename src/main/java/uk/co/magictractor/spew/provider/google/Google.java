package uk.co.magictractor.spew.provider.google;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.core.typeadapter.FractionTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.InstantTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;

// https://aaronparecki.com/oauth-2-simplified/#web-server-apps
//
//https://developers.google.com/identity/protocols/OAuth2InstalledApp
// https://developers.google.com/identity/protocols/OAuth2WebServer
//
// Uses OAuth2
//Client ID
//346766315499-60ikghor22r0lkbdtqp6jpgvtpff8vg3.apps.googleusercontent.com
//Client Secret
//-JW9p0euMrM-ymQgeqEJ1MvZ
public class Google implements SpewOAuth2ServiceProvider {

    // "2018-11-20T15:09:42Z"
    private static final DateTimeFormatter GOOGLE_INSTANT_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendZoneText(TextStyle.SHORT)
            .toFormatter()
            .withZone(ZoneOffset.UTC);

    private Google() {
    }

    @Override
    public String oauth2AuthorizationUri() {
        return "https://accounts.google.com/o/oauth2/v2/auth";
    }

    @Override
    public String oauth2TokenUri() {
        return "https://www.googleapis.com/oauth2/v4/token";
    }

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Arrays.asList(
            new InstantTypeAdapter(GOOGLE_INSTANT_FORMATTER),
            FractionTypeAdapter.NUMERIC);
    }

}
