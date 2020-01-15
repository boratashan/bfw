package b.currencycrawler.model;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link b.currencycrawler.model.CurrencyRates}.
 * NOTE: This class has been automatically generated from the {@link b.currencycrawler.model.CurrencyRates} original class using Vert.x codegen.
 */
public class CurrencyRatesConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CurrencyRates obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "currencyRateList":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<b.currencycrawler.model.CurrencyRate> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof JsonObject)
                list.add(new b.currencycrawler.model.CurrencyRate((JsonObject)item));
            });
            obj.setCurrencyRateList(list);
          }
          break;
        case "dateTime":
          if (member.getValue() instanceof String) {
            obj.setDateTime((String)member.getValue());
          }
          break;
        case "source":
          if (member.getValue() instanceof String) {
            obj.setSource((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(CurrencyRates obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(CurrencyRates obj, java.util.Map<String, Object> json) {
    if (obj.getCurrencyRateList() != null) {
      JsonArray array = new JsonArray();
      obj.getCurrencyRateList().forEach(item -> array.add(item.toJson()));
      json.put("currencyRateList", array);
    }
    if (obj.getDateTime() != null) {
      json.put("dateTime", obj.getDateTime());
    }
    if (obj.getSource() != null) {
      json.put("source", obj.getSource());
    }
  }
}
