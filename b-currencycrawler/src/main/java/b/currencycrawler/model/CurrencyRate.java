package b.currencycrawler.model;


import b.currencycrawler.verticles.CurrencyGeneratorVerticle;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

@DataObject(generateConverter = true)
public class CurrencyRate implements Serializable {
    private String currCode;
    private String time;
    private Double buy;

    public String getCurrCode() {
        return currCode;
    }

    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getBuy() {
        return buy;
    }

    public void setBuy(Double buy) {
        this.buy = buy;
    }

    public Double getSell() {
        return sell;
    }

    public void setSell(Double sell) {
        this.sell = sell;
    }

    private Double sell;


    public CurrencyRate() {
    }

    public CurrencyRate(String currCode, String time, Double buy, Double sell) {
        this.currCode = currCode;
        this.time = time;
        this.buy = buy;
        this.sell = sell;
    }

    public CurrencyRate(JsonObject jsonObject) {
        CurrencyRateConverter.fromJson(jsonObject, this);
    }


    public static CurrencyRate of(String currCode, String time, String buy, String sell) {
        return new CurrencyRate(currCode, time,
                Double.parseDouble(buy.replace(",", ".")),
                Double.parseDouble(sell.replace(",", ".")));
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        CurrencyRateConverter.toJson(this, jsonObject);
        return jsonObject;
    }






}
