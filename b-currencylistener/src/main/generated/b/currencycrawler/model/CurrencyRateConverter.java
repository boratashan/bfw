package b.currencycrawler.model;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link b.currencycrawler.model.CurrencyRate}.
 * NOTE: This class has been automatically generated from the {@link b.currencycrawler.model.CurrencyRate} original class using Vert.x codegen.
 */
public class CurrencyRateConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CurrencyRate obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "buy":
          if (member.getValue() instanceof Number) {
            obj.setBuy(((Number)member.getValue()).doubleValue());
          }
          break;
        case "currCode":
          if (member.getValue() instanceof String) {
            obj.setCurrCode((String)member.getValue());
          }
          break;
        case "sell":
          if (member.getValue() instanceof Number) {
            obj.setSell(((Number)member.getValue()).doubleValue());
          }
          break;
        case "time":
          if (member.getValue() instanceof String) {
            obj.setTime((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(CurrencyRate obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(CurrencyRate obj, java.util.Map<String, Object> json) {
    if (obj.getBuy() != null) {
      json.put("buy", obj.getBuy());
    }
    if (obj.getCurrCode() != null) {
      json.put("currCode", obj.getCurrCode());
    }
    if (obj.getSell() != null) {
      json.put("sell", obj.getSell());
    }
    if (obj.getTime() != null) {
      json.put("time", obj.getTime());
    }
  }
}
