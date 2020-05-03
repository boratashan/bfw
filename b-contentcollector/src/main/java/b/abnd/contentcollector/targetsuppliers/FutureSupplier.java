package b.abnd.contentcollector.targetsuppliers;

import io.vertx.core.Future;

import java.util.List;

public interface FutureSupplier<T> {
    Future<T> getNext();
    Future<List<T>> getAll();
}
