package b.contentcollector;

import b.contentcollector.targetsuppliers.InMemoryTargetSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

@SuppressWarnings("initialization.fields.uninitialized")
public class ContentDownloaderTest {
    ContentDownloader contentDownloader;
    @Before
    public void setUp() throws Exception {
        InMemoryTargetSupplier inMemoryTargetProvider = new InMemoryTargetSupplier();
        /*FileSystemAdapter adapter = new FileSystemAdapter();
        adapter
                .setFolder("")
                .setStrategy(FileSystemAdapter.FileNameSupplyingStrategy.UNIQUE_IDENTIFIER_WITH_EXTENSION)
                .setFileExtension(".html")
                .setOnFileStoringCompleteEvent(r -> {
                    System.out.printf("Content downloading is done, file is %s %n", r);
                });
        ContentDownloader contentDownloader = new ContentDownloader()
                .setTargetSupplier(inMemoryTargetProvider::get)
                .setAdapter(adapter);*/
    }

    @Test
    public void name() throws IOException {
        //collector.fetchAndDownloadContent();
    }

    @After
    public void tearDown() throws Exception {
    }
}