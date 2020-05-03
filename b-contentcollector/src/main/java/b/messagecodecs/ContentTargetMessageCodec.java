package b.messagecodecs;

import b.commons.model.ContentTarget;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class ContentTargetMessageCodec implements MessageCodec<ContentTarget, ContentTarget> {
    @Override
    public void encodeToWire(Buffer buffer, ContentTarget contentTarget) {
        String s =  Json.encode(contentTarget);
        int length = s.getBytes().length;
        buffer.appendString(s);
    }

    @Override
    public ContentTarget decodeFromWire(int pos, Buffer buffer) {
        int  length = buffer.getInt(pos);
        String o = buffer.getString(pos+4, length);
        ContentTarget t = Json.decodeValue(o, ContentTarget.class);
        return t;
    }

    @Override
    public ContentTarget transform(ContentTarget contentTarget) {
        return contentTarget;
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
