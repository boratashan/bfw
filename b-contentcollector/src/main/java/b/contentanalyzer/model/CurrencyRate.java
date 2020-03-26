package b.contentanalyzer.model;

import com.mybaas.utils.NumberUtils;

import java.time.LocalDateTime;

public class CurrencyRate {
    private Currency currency;
    private String currCode;
    private LocalDateTime datumDateTime;
    private Double buy;
    private Double sell;

    public static CurrencyRate valueOf(String buy, String sell) {
        return new CurrencyRate()
                .setBuy(NumberUtils.forceParsingFromString(buy))
                .setSell(NumberUtils.forceParsingFromString(sell));
    }

    public Currency getCurrency() {
        return currency;
    }

    public CurrencyRate setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public String getCurrCode() {
        return currCode;
    }

    public CurrencyRate setCurrCode(String currCode) {
        this.currCode = currCode;
        return this;
    }

    public LocalDateTime getDatumDateTime() {
        return datumDateTime;
    }

    public CurrencyRate setDatumDateTime(LocalDateTime datumDateTime) {
        this.datumDateTime = datumDateTime;
        return this;
    }

    public Double getBuy() {
        return buy;
    }

    public CurrencyRate setBuy(Double buy) {
        this.buy = buy;
        return this;
    }

    public Double getSell() {
        return sell;
    }

    public CurrencyRate setSell(Double sell) {
        this.sell = sell;
        return this;
    }
}
