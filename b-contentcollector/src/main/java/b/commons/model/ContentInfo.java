package b.commons.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;

@DataObject(generateConverter = true)
public class ContentInfo {
    private String id;
    private String sourceName;
    private String uri;
    private LocalDateTime timeOfDownload;
    private EncodeDecodeType encodeDecodeType;

    public String getId() {
        return id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getUri() {
        return uri;
    }

    public LocalDateTime getTimeOfDownload() {
        return timeOfDownload;
    }

    public EncodeDecodeType getEncodeDecodeType() {
        return encodeDecodeType;
    }

    public ContentInfo setSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    public ContentInfo setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public ContentInfo setTimeOfDownload(LocalDateTime timeOfDownload) {
        this.timeOfDownload = timeOfDownload;
        return this;
    }

    public ContentInfo setEncodeDecodeType(EncodeDecodeType encodeDecodeType) {
        this.encodeDecodeType = encodeDecodeType;
        return this;
    }

    public ContentInfo setId(String id) {
        this.id = id;
        return this;
    }

    public ContentInfo(JsonObject json) {
     //  ContentInfoConverter.fromJson(json, this);
    }

    public ContentInfo() {
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
      //  ContentInfoConverter.toJson(this, jsonObject);
        return jsonObject;
    }
}
