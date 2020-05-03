package b.downloadclock.verticles.model;


import b.commons.model.ContentTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContentTargetTest {
    ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test() throws JsonProcessingException {
        String source = "[{\"_id\":\"5e70e0e961cdcc56d424a603\",\"pcode\":\"C001\",\"project\":\"currencycollect\",\"target\":\"uzmanpara.com\",\"targeturi\":\"https://uzmanpara.milliyet.com.tr/doviz-kurlari/\",\"enabled\":true}]";


        ContentTarget[] target = mapper.readValue(source, ContentTarget[].class);

        System.out.println(target.toString());


    }
}