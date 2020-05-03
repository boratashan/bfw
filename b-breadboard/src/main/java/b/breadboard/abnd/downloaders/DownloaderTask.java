package b.breadboard.abnd.downloaders;

import b.breadboard.abnd.adapters.Adapter;
import b.breadboard.abnd.adapters.FileSystemAdapter;
import b.breadboard.abnd.concurrency.MultiTaskExecutor;
import b.breadboard.abnd.InMemoryTargetSupplier;
import b.downloadclock.verticles.model.ContentTarget;
import com.mybaas.utils.DateTimeFormat;
import com.mybaas.utils.DateTimeUtils;
import com.mybaas.utils.UIDUtils;

import java.util.ArrayList;
import java.util.function.Supplier;

public class DownloaderTask {

    private final Supplier supplier;
    private final MultiTaskExecutor<Boolean> multiTaskExecutor;

    private DownloaderTask(Supplier supplier) {
        this.supplier = supplier;
        multiTaskExecutor = new MultiTaskExecutor<>();
    }

    public static DownloaderTask withDefaultTargets() {
        ArrayList<ContentTarget> targets = new ArrayList<>();
/*        targets.add(new ContentTarget("fa7d57db-3112-41e8-b5c4-6409e9aeb0d1", "http://www.google.com"));
        targets.add(new Target("ac46a0be-030f-4666-bbab-c4b6cde3edc0", "http://www.yahoo.com"));
        targets.add(new Target("febc4068-081a-4a37-8de5-a4691e86abe2", "http://www.bing.com"));*/
        Supplier supplier = new InMemoryTargetSupplier(targets);
        DownloaderTask task = new DownloaderTask(supplier);
        return task;
    }

    private Adapter initAndGetAdapter() {
        return new FileSystemAdapter.Builder()
                .withFolder("c:/temp/")
                .withStrategy(FileSystemAdapter.FileNameSupplyingStrategy.CUSTOM)
                .withFileNameSupplier(() -> {
                    return String.format("%s_%s.%s", DateTimeUtils.nowString(DateTimeFormat.SIMPLEDATETIME), UIDUtils.getUniqueID(), ".cnt");
                })
                .withOnFileStoringCompleteEvent(file -> {
                })
                .build();
    }

    private ContentDownloader initAndGetContentDownloader(final Adapter adapter, final Supplier<ContentTarget> supplier) {
        return   new ContentDownloader.Builder()
                .withAdapter(adapter)
                .withTargetSupplier(supplier)
                .withOnDownloadCompleteEvent(downloadCompleteInfo -> {

                })
                .withOnDownloadErrorInfoEvent(downloadErrorInfo -> {

                })
                .build();
    }


    private void start() {
        multiTaskExecutor.submitAndRunTasks(() -> {
            Adapter adapter = this.initAndGetAdapter();
            Supplier<ContentTarget> supplier = this.supplier;
            ContentDownloader downloader = this.initAndGetContentDownloader(adapter, supplier);
            while (true) {
                downloader.downloadNext();
                Thread.sleep(1000*10);
            }
        }, 2);
    }


    private void stop() {
        multiTaskExecutor.shutDown();
        try {
            multiTaskExecutor.waitForTermination(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}
