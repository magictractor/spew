package uk.co.magictractor.spew.example.twitter.processor;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import com.google.common.collect.Iterables;

import uk.co.magictractor.spew.example.twitter.MyTwitterApp;
import uk.co.magictractor.spew.example.twitter.pojo.Tweet;
import uk.co.magictractor.spew.photo.Tag;
import uk.co.magictractor.spew.photo.TagType;
import uk.co.magictractor.spew.processor.ProcessorChain;
import uk.co.magictractor.spew.processor.SimpleProcessor;
import uk.co.magictractor.spew.processor.SimpleProcessorContext;
import uk.co.magictractor.spew.provider.twitter.TwitterUserTimelineIterator.UserTimelineIteratorBuilder;

public class TweetProcessor implements SimpleProcessor<Tweet> {

    private static final Collection<Tag> SUBJECT_TAGS = Tag.fetchTerminalTags(TagType.fetch("SUBJECT"));

    private static final Comparator<ScoreAndTweet> DEFAULT_COMPARATOR = Comparator
            .comparing(ScoreAndTweet::getScore)
            .reversed();

    private Function<Tweet, Integer> scoreFunction = Tweet::getLikes;
    private int targetScore = 100;
    private Set<ScoreAndTweet> highScoreTweets = new TreeSet<>(DEFAULT_COMPARATOR);

    @Override
    public void process(Tweet tweet, SimpleProcessorContext<Tweet> context) {
        int tweetScore = scoreFunction.apply(tweet);
        if (tweetScore >= targetScore) {
            highScoreTweets.add(new ScoreAndTweet(tweetScore, tweet));
        }
    }

    @Override
    public void afterProcessing(SimpleProcessorContext<Tweet> context) {
        Set<String> distinctSubjects = new LinkedHashSet<>();

        int rank = 1;
        for (ScoreAndTweet scoreAndTweet : highScoreTweets) {
            if (scoreAndTweet.subject != null) {
                distinctSubjects.add(scoreAndTweet.subject);
            }
            System.out.println(rank++ + "  " + scoreAndTweet.score + " " + scoreAndTweet.tweet);
        }

        System.out.println();
        rank = 1;
        for (String subject : distinctSubjects) {
            System.out.println(rank++ + "  " + subject);
        }
    }

    public static String findSubject(Tweet tweet) {
        // Same tag can appear twice in text when alias is used.
        Set<String> tags = new HashSet<>();

        String paddedTweetText = trimPadAndLower(tweet.getText());

        for (Tag subjectTag : SUBJECT_TAGS) {
            // plural of scientific names doesn't make sense, so just plural of name
            if (paddedTweetText.contains(padAndLower(plural(subjectTag.getTagName())))) {
                tags.add(subjectTag.getTagName());
            }
            else {
                for (String subject : subjectTag.getTagNameAndAliases()) {
                    if (paddedTweetText.contains(padAndLower(subject))) {
                        tags.add(subjectTag.getTagName());
                    }
                }
            }
        }

        if (tags.isEmpty()) {
            // TODO! mechanism for storing subject if not found or ambiguous
            System.err.println("Subject not found in " + tweet.getText());
            return null;
        }
        if (tags.size() > 1) {
            // TODO! mechanism for storing subject if not found or ambiguous
            System.err.println("Ambiguous subject: " + tags + " in '" + tweet + "'");
            return tags.iterator().next();
        }

        return Iterables.getOnlyElement(tags);
    }

    // TODO! something better, and (maybe) somewhere else
    private static String plural(String text) {
        if (text.endsWith("x") || text.endsWith("ch")) {
            return text + "es";
        }
        else {
            return text + "s";
        }
    }

    private static String trimPadAndLower(String text) {
        String trimmed = text.replace(".", "")
                .replace(",", "")
                .replace("!", "");
        // TODO! Only want/need to strip newlines when output results - with punctuation preserved
        //.replace("\n", " ")
        //.replace("\r", " ");
        return padAndLower(trimmed);
    }

    private static String padAndLower(String text) {
        return " " + text.toLowerCase() + " ";
    }

    private static final class ScoreAndTweet {
        private final int score;
        private final Tweet tweet;
        private String subject;

        public ScoreAndTweet(int score, Tweet tweet) {
            this.score = score;
            this.tweet = tweet;
            this.subject = findSubject(tweet);
        }

        public int getScore() {
            return score;
        }

        // TODO! add timestamp
        //        public int getScore() {
        //            return tweet.;
        //        }
    }

    public static void main(String[] args) {
        ProcessorChain processorChain = ProcessorChain.of(new TweetProcessor());

        Iterator<Tweet> iterator = new UserTimelineIteratorBuilder<>(MyTwitterApp.get(), Tweet.class).build();

        processorChain.execute(iterator, new SimpleProcessorContext<>());
    }

}
