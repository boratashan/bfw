package b.contentcollector.adapters;

import b.contentcollector.model.Content;
import b.contentcollector.model.ContentTarget;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public interface StorageAdapter {
     Future<Content> saveResponse(HttpResponse<Buffer> response, ContentTarget targetInfo);
}
