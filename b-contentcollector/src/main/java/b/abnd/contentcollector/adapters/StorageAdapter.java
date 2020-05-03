package b.abnd.contentcollector.adapters;


import b.commons.model.Content;
import b.commons.model.ContentTarget;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public interface StorageAdapter {
     Future<Content> saveResponse(HttpResponse<Buffer> response, ContentTarget targetInfo);
}
