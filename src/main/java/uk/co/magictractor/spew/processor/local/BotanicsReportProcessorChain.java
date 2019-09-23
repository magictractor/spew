package uk.co.magictractor.spew.processor.local;

import java.nio.file.Paths;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Iterator;

import com.google.common.collect.Iterators;

import uk.co.magictractor.spew.photo.Image;
import uk.co.magictractor.spew.photo.local.LocalPhoto;
import uk.co.magictractor.spew.photo.local.dates.DateRange;
import uk.co.magictractor.spew.photo.local.dates.DefaultLocalDirectoryDatesStrategy;
import uk.co.magictractor.spew.photo.local.dates.LocalDirectoryDatesStrategy;
import uk.co.magictractor.spew.photo.local.files.PathIterator;
import uk.co.magictractor.spew.processor.ProcessorChain;
import uk.co.magictractor.spew.processor.SimpleProcessorContext;

// Tidies tags, descriptions etc for photos already uploaded to Flickr.
public class BotanicsReportProcessorChain extends ProcessorChain<Image, Image, SimpleProcessorContext<Image>> {

    public BotanicsReportProcessorChain() {
        addProcessor(new BotanicsReportProcessor());
    }

    /**
     * Default date range is the current month if this is the last day of the
     * month, otherwise the previous month.
     */
    private static DateRange defaultReportDateRange(Clock clock) {
        LocalDate today = LocalDate.now(clock);
        LocalDate dateInRange = today.plusDays(1).minusMonths(1);

        // TODO! temp
        // return DateRange.forMonth(dateInRange.getYear(), dateInRange.getMonthValue());
        return DateRange.forDay(dateInRange.getYear(), dateInRange.getMonthValue(), 2);
    }

    public static void main(String[] args) {
        LocalDirectoryDatesStrategy s = new DefaultLocalDirectoryDatesStrategy();

        // TODO! introduce a LocalImageLibrary
        PathIterator pathIterator = new PathIterator(Paths.get("C:\\Users\\Ken\\Pictures"));
        pathIterator.setFileFilter(LocalPhoto::isPhoto);
        pathIterator.setDirectoryFilter((path) -> s.test(path, defaultReportDateRange(Clock.systemDefaultZone())));

        Iterator<LocalPhoto> photoIterator = Iterators.transform(pathIterator, LocalPhoto::new);

        BotanicsReportProcessorChain processorChain = new BotanicsReportProcessorChain();
        // processorChain.execute(new LocalPhotoIterator(), new
        // SimpleProcessorContext<>());
        processorChain.execute(photoIterator, new SimpleProcessorContext<>());
    }

}
