package com.mybaas.commons.proxyclasses;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.mybaas.commons.proxyclasses.PaginationRequest}.
 * NOTE: This class has been automatically generated from the {@link com.mybaas.commons.proxyclasses.PaginationRequest} original class using Vert.x codegen.
 */
public class PaginationRequestConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, PaginationRequest obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "pageNumber":
          if (member.getValue() instanceof Number) {
            obj.setPageNumber(((Number)member.getValue()).intValue());
          }
          break;
        case "size":
          if (member.getValue() instanceof Number) {
            obj.setSize(((Number)member.getValue()).intValue());
          }
          break;
        case "valid":
          break;
      }
    }
  }

  public static void toJson(PaginationRequest obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(PaginationRequest obj, java.util.Map<String, Object> json) {
    json.put("pageNumber", obj.getPageNumber());
    json.put("size", obj.getSize());
    json.put("valid", obj.isValid());
  }
}
