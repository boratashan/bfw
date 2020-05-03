package b.breadboard.abnd.downloaders;


import b.breadboard.abnd.adapters.Adapter;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * 1)Set target url
 * CollecterBuilder
 * .setUrl("www.url.com/")
 * .setTarget(FILE)
 * .setAdapter(FolderAdapterBuilder.setfolder("/etc/")
 * .build();
 */
@SuppressWarnings("initialization.fields.uninitialized")
public class ContentDownloader {
    private Adapter adapter;
    private Supplier<ContentTarget> targetSupplier;
    private Consumer<DownloadCompleteInfo> onDownloadCompleteEvent;
    private Consumer<DownloadErrorInfo> onDownloadErrorInfoEvent;

    private ContentDownloader(Builder builder) {
        setAdapter(builder.adapter);
        setTargetSupplier(builder.targetSupplier);
        setOnDownloadCompleteEvent(builder.onDownloadCompleteEvent);
        onDownloadErrorInfoEvent = builder.onDownloadErrorInfoEvent;
    }

    public ContentDownloader setAdapter(Adapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public ContentDownloader setTargetSupplier(Supplier<ContentTarget> targetSupplier) {
        this.targetSupplier = targetSupplier;
        return this;
    }

    public ContentDownloader setOnDownloadCompleteEvent(Consumer<DownloadCompleteInfo> onDownloadCompleteEvent) {
        this.onDownloadCompleteEvent = onDownloadCompleteEvent;
        return this;
    }

    public void downloadNext() {
        ContentTarget target = targetSupplier.get();
        try {
            if (Objects.nonNull(target)) {
                this.downloadContent(target);
            }
        }
        catch (IOException e){
            if (Objects.nonNull(onDownloadErrorInfoEvent)) {
                onDownloadErrorInfoEvent.accept(new DownloadErrorInfo(target, e));
            }
        }
    }

    public void downloadContents() {
        ContentTarget target = targetSupplier.get();
        while (Objects.nonNull(target)) {
            try {
                this.downloadContent(target);
            }
            catch (IOException e){
                if (Objects.nonNull(onDownloadErrorInfoEvent)) {
                    onDownloadErrorInfoEvent.accept(new DownloadErrorInfo(target, e));
                }
            }
            target = targetSupplier.get();
        }
    }

    private void downloadContent(ContentTarget target) throws IOException {
        URL url =   new URL(target.getTargeturi());
        try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream())) {
            File downloaded = adapter.read(inputStream);
            if (Objects.nonNull(onDownloadCompleteEvent)) {
                onDownloadCompleteEvent.accept(new DownloadCompleteInfo(url, downloaded.getAbsolutePath()));
            }
        }
    }

    public class DownloadCompleteInfo {
        private URL source;
        private String fileStored;

        public DownloadCompleteInfo(URL source, String fileStored) {
            this.source = source;
            this.fileStored = fileStored;
        }

        public URL getSource() {
            return source;
        }

        public String getFileStored() {
            return fileStored;
        }
    }

    public class DownloadErrorInfo {
        ContentTarget target;
        Exception exception;

        public DownloadErrorInfo(ContentTarget target, Exception exception) {
            this.target = target;
            this.exception = exception;
        }

        public ContentTarget getTarget() {
            return target;
        }

        public Exception getException() {
            return exception;
        }
    }

    public static final class Builder {
        private Adapter adapter;
        private Supplier<ContentTarget> targetSupplier;
        private Consumer<DownloadCompleteInfo> onDownloadCompleteEvent;
        private Consumer<DownloadErrorInfo> onDownloadErrorInfoEvent;

        public Builder() {
        }

        public Builder withAdapter(Adapter val) {
            adapter = val;
            return this;
        }

        public Builder withTargetSupplier(Supplier<ContentTarget> val) {
            targetSupplier = val;
            return this;
        }

        public Builder withOnDownloadCompleteEvent(Consumer<DownloadCompleteInfo> val) {
            onDownloadCompleteEvent = val;
            return this;
        }

        public Builder withOnDownloadErrorInfoEvent(Consumer<DownloadErrorInfo> val) {
            onDownloadErrorInfoEvent = val;
            return this;
        }

        public ContentDownloader build() {
            return new ContentDownloader(this);
        }
    }
}
