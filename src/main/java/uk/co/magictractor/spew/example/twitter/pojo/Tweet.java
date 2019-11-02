package uk.co.magictractor.spew.example.twitter.pojo;

import java.time.LocalDateTime;

import com.google.common.base.MoreObjects;

public class Tweet {

    private String id_str;
    //private String id_str;
    // "text" for truncated tweets, "full_text" when extended
    private String full_text;
    private int favorite_count;
    private int retweet_count;
    // "created_at": "Tue May 28 12:43:55 +0000 2019"
    // would Instant be better?
    // private Instant created_at;
    private LocalDateTime created_at;

    public String getServiceProviderId() {
        return id_str;
    }

    public String getText() {
        return full_text;
    }

    public int getLikes() {
        return favorite_count;
    }

    public int getRetweets() {
        return retweet_count;
    }

    public LocalDateTime getCreatedAt() {
        // public Instant getCreatedAt() {
        return created_at;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("likes", getLikes())
                .add("retweets", getRetweets())
                .add("text", getText())
                .toString();
    }

}
