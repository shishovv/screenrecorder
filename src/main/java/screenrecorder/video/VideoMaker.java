package screenrecorder.video;

import org.jetbrains.annotations.NotNull;

import javax.media.*;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.FileTypeDescriptor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VideoMaker implements ControllerListener, DataSinkListener {

    @NotNull
    private final Path outFile;
    @NotNull
    private final Lock lock;
    @NotNull
    private final Condition completionCondition;
    private volatile boolean allCompleted;

    private VideoMaker(@NotNull final Path outFile) {
        this.outFile = outFile;
        lock = new ReentrantLock();
        completionCondition = lock.newCondition();
    }

    @NotNull
    public static VideoMaker newVideoMaker(@NotNull final String outFile) {
        return new VideoMaker(Paths.get(outFile));
    }

    public void makeVideo(@NotNull final VideoParams params,
                          @NotNull final List<BufferedImage> images) {
        startProcessing(createProcessor(params, images));
        awaitAllCompleted();
    }

    @Override
    public void controllerUpdate(final ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ConfigureCompleteEvent) {
            onProcessorConfigured((Processor) controllerEvent.getSourceController());
        } else if (controllerEvent instanceof RealizeCompleteEvent) {
            onProcessorRealized((Processor) controllerEvent.getSourceController());
        } else if (controllerEvent instanceof EndOfMediaEvent) {
            onCompleted(controllerEvent.getSourceController());
        } else if (controllerEvent instanceof ResourceUnavailableEvent) {
            throw new RuntimeException();
        }
    }

    @Override
    public void dataSinkUpdate(@NotNull final DataSinkEvent dataSinkEvent) {
        if (dataSinkEvent instanceof EndOfStreamEvent) {
            ((DataSink) dataSinkEvent.getSource()).close();
        } else if (dataSinkEvent instanceof DataSinkErrorEvent) {
            throw new RuntimeException();
        }
    }

    private void startProcessing(@NotNull final Processor processor) {
        processor.addControllerListener(this);
        processor.configure();
    }

    private void onProcessorConfigured(@NotNull final Processor processor) {
        processor.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
//        final TrackControl tcs[] = processor.getTrackControls();
//        final Format formats[] = tcs[0].getSupportedFormats();
//        if (formats == null || formats.length <= 0) {
//            throw new RuntimeException("The mux does not support the input format: " + tcs[0].getFormat());
//        }
//        tcs[0].setFormat(formats[0]);
        processor.realize();
    }

    private void onProcessorRealized(@NotNull final Processor processor) {
        final DataSink dataSink = createDateSink(processor);
        dataSink.addDataSinkListener(this);
        try {
            dataSink.open();
            processor.start();
            dataSink.start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void onCompleted(@NotNull final Controller controller) {
        allCompleted = true;
        releaseController(controller);
        notifyAllCompleted();
    }

    private void releaseController(@NotNull final Controller controller) {
        controller.stop();
        controller.close();
    }

    private void awaitAllCompleted() {
        lock.lock();
        try {
            while (!allCompleted) {
                completionCondition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private void notifyAllCompleted() {
        lock.lock();
        try {
            completionCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    private Processor createProcessor(@NotNull final VideoParams params, @NotNull final List<BufferedImage> images) {
        try {
            return Manager.createProcessor(ImageDataSource.newDataSource(params, images));
        } catch (NoProcessorException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private DataSink createDateSink(@NotNull final Processor processor) {
        try {
            return Manager.createDataSink(processor.getDataOutput(),
                    new MediaLocator("file:///" + outFile.toString()));
        } catch (NoDataSinkException e) {
            throw new RuntimeException(e);
        }
    }
}
