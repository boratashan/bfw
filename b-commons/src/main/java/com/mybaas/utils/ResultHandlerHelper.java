package com.mybaas.utils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.function.Function;

public class ResultHandlerHelper {

    public static  <T, R> Handler<AsyncResult<T>> convertCallback(Handler<AsyncResult<R>> resultHandler, Function<T, R> converter) {
        return event -> {
            if (event.succeeded()) {
                resultHandler.handle(Future.succeededFuture(converter.apply(event.result())));
            }
            else if (event.failed()) {
                resultHandler.handle(Future.failedFuture("Fail"));
            }
        };
    }

}
