package b.contentcollector.targetsuppliers;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InMemoryTargetSupplier implements Supplier<Target> {
    private List<Target> targets;
    private int ptr = 0;
    boolean isInfiniteGet = false;

    public InMemoryTargetSupplier() {
        targets = new ArrayList<>();
        try {
            targets.add(new Target("925081d4-1cd2-45bc-9553-8312b9ac98bd", "http://www.google.com"));
            targets.add(new Target("a2b0e824-d2c8-47ae-a8b7-92ecb284225d", "http://www.yahoo.com"));
            targets.add(new Target("00d225a2-d5d9-459c-ac6e-4f5b2d9b8dc0", "http://www.bing.com"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public InMemoryTargetSupplier(boolean isInfiniteGet){
        this();
        this.isInfiniteGet = isInfiniteGet;
    }

    @Override
    public synchronized Target get() {
        if (targets.isEmpty() || (this.isInfiniteGet && (ptr >= targets.size()))) {
                return null;
        }
        Target res = targets.get(ptr);
        if (isInfiniteGet)
            ptr = ++ptr % targets.size();
        else
            ptr++;
        return res;
    }

}
