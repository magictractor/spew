package uk.co.magictractor.spew.twitter.pojo;

import com.google.common.base.MoreObjects;

public class Tweet {

    // "created_at": "Tue May 28 12:43:55 +0000 2019"
    //   "id_str": "1133353205616783360",

    private long id;
    //private String id_str;
    // "text" for truncated tweets, "full_text" when extended
    private String full_text;
    private int favorite_count;
    private int retweet_count;

    public long getId() {
        return id;
    }

    public String getText() {
        return full_text;
    }

    public int getLikes() {
        return favorite_count;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("likes", getLikes())
                .add("text", getText())
                .toString();
    }

}
