package b.contentcollector;


import b.contentcollector.adapters.Adapter;
import b.contentcollector.targetsuppliers.Target;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 1)Set target url
 *  CollecterBuilder
 *      .setUrl("www.url.com/")
 *      .setTarget(FILE)
 *      .setAdapter(FolderAdapterBuilder.setfolder("/etc/")
 *  .build();
 */
@SuppressWarnings("initialization.fields.uninitialized")
public class ContentDownloader {
    private Adapter adapter;
    private Supplier<Target> targetSupplier;
    private Consumer<String> downloadCompleteConsumer;

    public ContentDownloader setAdapter(Adapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public ContentDownloader setTargetSupplier(Supplier<Target> targetSupplier) {
        this.targetSupplier = targetSupplier;
        return this;
    }

    public ContentDownloader setDownloadCompleteConsumer(Consumer<String> downloadCompleteConsumer) {
        this.downloadCompleteConsumer = downloadCompleteConsumer;
        return this;
    }

    public void downloadContents() throws IOException {
        Target target = targetSupplier.get();
        while(Objects.nonNull(target)) {
            this.downloadContent(target);
            target = targetSupplier.get();
        }
    }

    private void downloadContent(Target target) throws IOException {
        URL url = target.getUrl();
        if (Objects.nonNull(url)) {
            try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream())) {
                adapter.read(inputStream);
            }
        }
    }


}
