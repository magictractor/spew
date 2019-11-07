package uk.co.magictractor.spew.processor.common;

import uk.co.magictractor.spew.photo.local.LocalLibrary;
import uk.co.magictractor.spew.photo.local.LocalPhoto;
import uk.co.magictractor.spew.processor.MediaProcessor;

/**
 * Replace default titles based on the file name, "IMG_1234" etc, with the name
 * from the subject tag.
 */
public class LocalPhotoSyncProcessor implements MediaProcessor {

    private final LocalLibrary localLibrary = LocalLibrary.get();

    @Override
    public void process(MutableMedia remotePhoto, MediaProcessorContext context) {
        // TODO! need to tidy use of Media/Photo. MutablePhoto implements Media, not Photo
        LocalPhoto localPhoto = localLibrary.findLocalPhoto(remotePhoto);
    }

}
