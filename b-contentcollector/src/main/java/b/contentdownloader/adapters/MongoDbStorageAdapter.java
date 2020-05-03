package b.contentdownloader.adapters;

import b.commons.model.Content;
import b.commons.model.ContentTarget;
import b.commons.model.EncodeDecodeType;
import com.mybaas.utils.DateTimeFormat;
import com.mybaas.utils.DateTimeUtils;
import com.mybaas.utils.StringUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.HttpResponse;

import java.time.LocalDateTime;

public class MongoDbStorageAdapter implements StorageAdapter {

    private MongoClient mongoClient;
    private EncodeDecodeType encodeDecodeType;

    private MongoDbStorageAdapter(Builder builder) {
        mongoClient = builder.mongoClient;
        encodeDecodeType = builder.encodeDecodeType;
    }


    public Future<Content> saveResponse(HttpResponse<Buffer> response, ContentTarget targetInfo) {
        Promise<Content> promise = Promise.promise();
        String res;
        switch (encodeDecodeType) {
            case NONE:
                res = response.bodyAsString();
                break;
            case BASE64:
                res = StringUtils.encodeByBase64(response.bodyAsString());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + encodeDecodeType);
        }
        Content content = new Content();
        content.setPcode(targetInfo.getPcode());
        content.setEncoding(EncodeDecodeType.BASE64);
        content.setTarget(targetInfo.getTarget());
        content.setProject(targetInfo.getProject());
        content.setTargeturi(targetInfo.getTargeturi());
        content.setProcessed(false);
        content.setSize(res.getBytes().length);
        content.setDownloadtime(LocalDateTime.now());

        JsonObject document = new JsonObject()
                .put("pcode", content.getPcode())
                .put("project", content.getProject())
                .put("target", content.getTarget())
                .put("targeturi", content.getTargeturi())
                .put("encoding", content.getEncoding().toString())
                .put("processed", false)
                .put("downloadtime", new JsonObject().put("$date", DateTimeUtils.toString(content.getDownloadtime(), DateTimeFormat.UTCDATETIME)))
                .put("size", content.getSize())
                .put("content", new JsonObject().put("$binary", res));

        mongoClient.save("contents", document,event -> {
            if (event.succeeded()) {
                content.setId(event.result());
                promise.complete(content);
            }
            else {
                promise.fail(event.cause());
            }
        });
        return promise.future();
    }



    public static final class Builder {
        private MongoClient mongoClient;
        private EncodeDecodeType encodeDecodeType;

        public Builder() {
        }

        public Builder withMongoClient(MongoClient val) {
            mongoClient = val;
            return this;
        }

        public Builder withEncodeDecodeType(EncodeDecodeType val) {
            encodeDecodeType = val;
            return this;
        }

        public MongoDbStorageAdapter build() {
            return new MongoDbStorageAdapter(this);
        }
    }
}
