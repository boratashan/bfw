package b.currencycrawler.crawler;

import b.currencycrawler.model.CurrencyRate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.spi.CurrencyNameProvider;


public class DefaultCurrencyRateCrawler implements  CurrencyRateCrawler {

    private static final String URL_TO_CRAWL = "https://kur.doviz.com/serbest-piyasa";

    @Override
    public List<CurrencyRate> buildRates() throws CurrencyRateCrawlerException {

        try {
            Document doc = Jsoup.connect(URL_TO_CRAWL).get();

            ArrayList rates = new ArrayList();

            Element body = doc.getElementById("currencies");Elements elems =   body.getElementsByAttribute("data-table-subpage-key");
           for(Element e : elems) {
                String currCode = e.attributes().get("data-table-subpage-key");
                String time = e.getAllElements().get(9).html();
                String buy = e.getAllElements().get(4).html();
                String sell = e.getAllElements().get(5).html();
                rates.add(CurrencyRate.of(currCode, time, buy, sell));
            }
            return rates;
        } catch (Exception e) {
            throw new CurrencyRateCrawlerException(String.format("Error while crawling %s", URL_TO_CRAWL), e);
        }
    }

}
