package b.contentanalyzer.contentparsers;

import b.commons.model.Content;
import b.contentanalyzer.model.CurrencyRate;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface ContentParser {

    void parse(Content content, Handler<AsyncResult<List<CurrencyRate>>> handler);
}
