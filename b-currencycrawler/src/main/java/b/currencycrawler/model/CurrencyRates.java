package b.currencycrawler.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.List;

@DataObject(generateConverter = true)
public class CurrencyRates implements Serializable {

    private String dateTime;
    private String source;
    private List<CurrencyRate> currencyRateList;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<CurrencyRate> getCurrencyRateList() {
        return currencyRateList;
    }

    public void setCurrencyRateList(List<CurrencyRate> currencyRateList) {
        this.currencyRateList = currencyRateList;
    }


    public CurrencyRates() {
    }

    public CurrencyRates(JsonObject jsonObject) {
        CurrencyRatesConverter.fromJson(jsonObject, this);
    }


    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        CurrencyRatesConverter.toJson(this, jsonObject);
        return jsonObject;
    }
}
