package b.currencycrawler.crawler;

import b.currencycrawler.model.CurrencyRate;

import java.util.Currency;
import java.util.List;

public interface CurrencyRateCrawler {


    public List<CurrencyRate> buildRates() throws CurrencyRateCrawlerException;

}
