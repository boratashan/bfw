package b.contentanalyzer.analyzers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class UzmprAnalyzerTest {



    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void analyze() throws IOException {
        String path = "src/test/resources/test/uzmpara.html";
        File file = new File(path);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        Document document = Jsoup.parse(sb.toString());
        String s = document.select("body > div:nth-child(9) > div.detMain.borsaMain > div.detL > div.box.box7.box11 > table > tbody > tr:nth-child(2)").toString();

        System.out.println(s);
        s = document.select("body > div:nth-child(9) > div.detMain.borsaMain > div.detL > div.box.box7.box11 > table > tbody > tr:nth-child(3)").toString();
        System.out.println(s);

        Elements elems = document.select("body > div:nth-child(9) > div.detMain.borsaMain > div.detL > div.box.box7.box11 > table > tbody > tr:nth-child(2) > td:nth-child(3)");

        System.out.println(elems.toString());

    }
}