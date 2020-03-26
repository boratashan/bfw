package b.contentanalyzer.contentparsers;

import b.contentanalyzer.model.Currency;
import b.contentanalyzer.model.CurrencyRate;
import b.contentcollector.model.Content;
import io.vertx.core.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CurrencyContentParser implements ContentParser {

    private CurrencyContentMapper contentMapper;

    public CurrencyContentParser(CurrencyContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }

    @Override
    public void parse(Content content, Handler<AsyncResult<List<CurrencyRate>>> handler) {
        List<Future> listOfFutures = new ArrayList<>();
        List<CurrencyRate> currencyRates = new ArrayList<>();
        Document document = Jsoup.parse(content.getContent());
        Arrays.stream(Currency.values()).forEach(currency -> {
            Future<Optional<Pair<String, String>>> future = contentMapper.getBuyAndSellSelector(content.getProject(), content.getTarget(), currency);
            listOfFutures.add(future);
            future.setHandler(ar -> {
                try {
                    if (ar.succeeded()) {
                        Optional<Pair<String, String>> pair = ar.result();
                        if (pair.isPresent()) {
                            String buy = document.select(pair.get().getLeft()).text();
                            String sell = document.select(pair.get().getRight()).text();
                            CurrencyRate currencyRate = CurrencyRate.valueOf(buy, sell)
                                    .setCurrCode(currency.toString())
                                    .setDatumDateTime(LocalDateTime.now())
                                    .setCurrency(currency);
                            currencyRates.add(currencyRate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        CompositeFuture.all(listOfFutures).setHandler(event ->  {
            if (event.succeeded()) {
                handler.handle(Future.succeededFuture(currencyRates));
            }
        });
    }


}
