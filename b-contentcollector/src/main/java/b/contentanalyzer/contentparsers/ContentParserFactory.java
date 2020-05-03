package b.contentanalyzer.contentparsers;


import b.commons.model.Content;
import io.vertx.ext.mongo.MongoClient;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;

public class ContentParserFactory {


    private static HashMap<Class, ContentParser> objectCache
            = new HashMap<>();

    private static boolean checkSource(Content content) {
        return ((content.getTarget().equalsIgnoreCase("uzmanpara.com"))
                ||
                (content.getTarget().equalsIgnoreCase("dovizcom"))
                ||
                (content.getTarget().equalsIgnoreCase("bloomberght")));
    }

    public static synchronized ContentParser getParser(Content content, MongoClient mongoClient) {
        if (checkSource(content)) {
            if (!objectCache.containsKey(CurrencyContentParser.class))
               objectCache.putIfAbsent(CurrencyContentParser.class, new CurrencyContentParser(new CurrencyContentMapper(mongoClient)));
            return objectCache.get(CurrencyContentParser.class);
        }
        else {
            throw new NotImplementedException("Not implemented yet");
        }
    }
}
