package b.contentcollector;

import b.abnd.adapters.FileSystemAdapter;
import b.abnd.downloaders.ContentDownloader;
import b.abnd.InMemoryTargetSupplier;
import b.contentcollector.model.ContentTarget;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;


@SuppressWarnings("initialization.fields.uninitialized")
public class ContentDownloaderTest {
    private static File tempDirectory;
    private ContentDownloader contentDownloader;
    private List<String> listOfDownloadedFiles;

    @BeforeClass
    public static void beforeClass() throws IOException {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();
        System.out.printf("Temp directory %s is created \n", tempDirectory.getAbsolutePath());
    }

    @AfterClass
    public static void afterClass() {
        System.out.printf("Removing temp directory %s \n", tempDirectory.getAbsolutePath());
        FileUtils.deleteQuietly(tempDirectory);
        System.out.printf("Removing temp directory %s [Done] \n", tempDirectory.getAbsolutePath());
    }

    private List<ContentTarget> getProperTestTargets() {
        List<ContentTarget> targets = new ArrayList<>();

      /*  targets.add(new Target("925081d4-1cd2-45bc-9553-8312b9ac98bd", "http://www.google.com"));
        targets.add(new Target("a2b0e824-d2c8-47ae-a8b7-92ecb284225d", "http://www.yahoo.com"));
        targets.add(new Target("00d225a2-d5d9-459c-ac6e-4f5b2d9b8dc0", "http://www.bing.com"));
*/
        return targets;
    }

    private List<ContentTarget> getUnProperTestTargets() {
        List<ContentTarget> targets = new ArrayList<>();
     /*       targets.add(new Target("925081d4-1cd2-45bc-9553-8312b9ac98bd", "http://nonte-www.google.com"));
            targets.add(new Target("a2b0e824-d2c8-47ae-a8b7-92ecb284225d", "http://www.yahoo.com"));
            targets.add(new Target("00d225a2-d5d9-459c-ac6e-4f5b2d9b8dc0", "http://www.bing.com"));
*/
        return targets;
    }

    @Ignore
    @Test
    public void testFileDownloads() throws Exception {
        listOfDownloadedFiles = new ArrayList<>();
        InMemoryTargetSupplier targetSupplier = new InMemoryTargetSupplier(this.getProperTestTargets(), false);
        FileSystemAdapter adapter = new FileSystemAdapter.Builder()
                .withFolder(tempDirectory.getAbsolutePath())
                .withStrategy(FileSystemAdapter.FileNameSupplyingStrategy.UNIQUE_IDENTIFIER_WITH_EXTENSION)
                .withFileExtension(".html")
                .withOnFileStoringCompleteEvent(r -> {

                    System.out.printf("Content downloading is done, file is %s %n", r);
                })
                .build();
        contentDownloader = new ContentDownloader.Builder()
                .withTargetSupplier(targetSupplier)
                .withAdapter(adapter)
                .withOnDownloadCompleteEvent(f ->
                        listOfDownloadedFiles.add(f.getFileStored())
                )
                .withOnDownloadErrorInfoEvent(err -> System.out.printf("url %s is broken, exception -> %s \n", err.getTarget().getTargeturi().toString(), err.getException().getMessage()))
                .build();


        contentDownloader.downloadContents();

        boolean passed = Arrays.asList(tempDirectory.list()).containsAll(listOfDownloadedFiles);
//        assertTrue("Content downloader downloaded files count is not equal to list of saved files", passed);
    }

    @Ignore
    @Test
    public void testFileDownloadsExceptions() throws Exception {
        listOfDownloadedFiles = new ArrayList<>();
        InMemoryTargetSupplier targetSupplier = new InMemoryTargetSupplier(this.getUnProperTestTargets(), false);
        FileSystemAdapter adapter = new FileSystemAdapter.Builder()
                .withFolder(tempDirectory.getAbsolutePath())
                .withStrategy(FileSystemAdapter.FileNameSupplyingStrategy.UNIQUE_IDENTIFIER_WITH_EXTENSION)
                .withFileExtension(".html")
                .withOnFileStoringCompleteEvent(r -> {
                    System.out.printf("Content downloading is done, file is %s %n", r);
                })
                .build();
        AtomicInteger exceptionCounter = new AtomicInteger(0);
        contentDownloader = new ContentDownloader.Builder()
                .withTargetSupplier(targetSupplier)
                .withAdapter(adapter)
                .withOnDownloadCompleteEvent(f ->
                        listOfDownloadedFiles.add(f.getFileStored())
                )
                .withOnDownloadErrorInfoEvent(err -> {
                    exceptionCounter.incrementAndGet();
                })
                .build();
        contentDownloader.downloadContents();
        boolean passed = exceptionCounter.get() > 0;
        assertTrue("Content downloader downloaded files count is not equal to list of saved files", passed);
    }


    @After
    public void tearDown() {
        listOfDownloadedFiles.clear();
    }

}