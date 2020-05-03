package b.commons.model;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link b.commons.model.ContentInfo}.
 * NOTE: This class has been automatically generated from the {@link b.commons.model.ContentInfo} original class using Vert.x codegen.
 */
public class ContentInfoConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ContentInfo obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "encodeDecodeType":
          if (member.getValue() instanceof String) {
            obj.setEncodeDecodeType(b.commons.model.EncodeDecodeType.valueOf((String)member.getValue()));
          }
          break;
        case "id":
          if (member.getValue() instanceof String) {
            obj.setId((String)member.getValue());
          }
          break;
        case "sourceName":
          if (member.getValue() instanceof String) {
            obj.setSourceName((String)member.getValue());
          }
          break;
        case "uri":
          if (member.getValue() instanceof String) {
            obj.setUri((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(ContentInfo obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(ContentInfo obj, java.util.Map<String, Object> json) {
    if (obj.getEncodeDecodeType() != null) {
      json.put("encodeDecodeType", obj.getEncodeDecodeType().name());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getSourceName() != null) {
      json.put("sourceName", obj.getSourceName());
    }
    if (obj.getUri() != null) {
      json.put("uri", obj.getUri());
    }
  }
}
