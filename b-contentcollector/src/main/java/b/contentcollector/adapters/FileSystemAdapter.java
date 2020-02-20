package b.contentcollector.adapters;

import b.utils.FileAndFolderUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class FileSystemAdapter implements Adapter {
    public enum FileNameSupplyingStrategy {
        UNIQUE_IDENTIFIER,
        UNIQUE_IDENTIFIER_WITH_EXTENSION,
        CUSTOM
    }

    private static final String CONSTRUCTOR_VALIDATION_EXCEPTION_MESSAGE = "%s is not defined but strategy is %s";

    private final String folder;
    private final FileNameSupplyingStrategy strategy;
    private final Supplier<String> fileNameSupplier;
    private  String fileExtension;
    private Consumer<String> onFileStoringCompleteEvent;

    private FileSystemAdapter(Builder builder) {
        folder = builder.folder;
        strategy = builder.strategy;
        fileNameSupplier = builder.fileNameSupplier;
        fileExtension = builder.fileExtension;
        onFileStoringCompleteEvent = builder.onFileStoringCompleteEvent;

        switch (strategy) {
            case UNIQUE_IDENTIFIER:
                break;
            case UNIQUE_IDENTIFIER_WITH_EXTENSION:
                if (Objects.isNull(fileExtension))
                    throw new IllegalStateException(String.format(CONSTRUCTOR_VALIDATION_EXCEPTION_MESSAGE, "File extension", strategy.name()));
                break;
            case CUSTOM:
                if (Objects.isNull(fileNameSupplier))
                    throw new IllegalStateException(String.format(CONSTRUCTOR_VALIDATION_EXCEPTION_MESSAGE, "File name supplier", strategy.name()));
                break;
        }
    }


    private String generateNewUID() {
        return UUID.randomUUID().toString();
    }

    public String getNewFileName() {
        switch (strategy) {
            case UNIQUE_IDENTIFIER:
                return generateNewUID();
            case UNIQUE_IDENTIFIER_WITH_EXTENSION:
                return String.format("%s", FileAndFolderUtils.includeFileExtension(generateNewUID(), this.fileExtension));
            case CUSTOM:
                return fileNameSupplier.get();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void read(InputStream inputStream) throws IOException {
        String fileName = getNewFileName();
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        try  {
            inputStream.transferTo(fileOutputStream);
            fileOutputStream.flush();
            if (Objects.nonNull(onFileStoringCompleteEvent)) {
                this.onFileStoringCompleteEvent.accept(fileName);
            }
        }
        finally {
            fileOutputStream.close();
        }
    }

    public static final class Builder {
        private String folder;
        private FileNameSupplyingStrategy strategy;
        private Supplier<String> fileNameSupplier;
        private String fileExtension;
        private Consumer<String> onFileStoringCompleteEvent;

        public Builder() {
        }

        public Builder withFolder(String val) {
            folder = val;
            return this;
        }

        public Builder withStrategy(FileNameSupplyingStrategy val) {
            strategy = val;
            return this;
        }

        public Builder withFileNameSupplier(Supplier<String> val) {
            fileNameSupplier = val;
            return this;
        }

        public Builder withFileExtension(String val) {
            fileExtension = val;
            return this;
        }

        public Builder withOnFileStoringCompleteEvent(Consumer<String> val) {
            onFileStoringCompleteEvent = val;
            return this;
        }

        public FileSystemAdapter build() {
            return new FileSystemAdapter(this);
        }
    }
}
