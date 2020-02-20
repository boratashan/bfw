package b.contentcollector;


import b.contentcollector.adapters.FileSystemAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("initialization.fields.uninitialized")
public class FileSystemAdapterTest {
    private FileSystemAdapter adapter;

    @Before
    public void setUp() throws Exception {
        adapter = new FileSystemAdapter
                .Builder()
                .withFolder("c:/temp")
                .withFileExtension(".html")
                .withStrategy(FileSystemAdapter.FileNameSupplyingStrategy.UNIQUE_IDENTIFIER_WITH_EXTENSION)
                .build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testWithExtension() {
        /*
        FileSystemAdapter adapter = new FileSystemAdapter()
                .setFolder("temp")
                .setStrategy(FileSystemAdapter.FileNameSupplyingStrategy.UNIQUE_IDENTIFIER_WITH_EXTENSION)
                .setFileExtension(".cnt");
        String fileName = adapter.getNewFileName();
        if (fileName.endsWith(".cnt")) {
            Assert.assertTrue("Success", true);
        }

         */
    }
}
