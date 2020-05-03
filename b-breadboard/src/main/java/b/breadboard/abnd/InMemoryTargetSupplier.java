package b.breadboard.abnd;

import b.downloadclock.verticles.model.ContentTarget;

import java.util.List;
import java.util.function.Supplier;

public class InMemoryTargetSupplier implements Supplier<ContentTarget> {
    private List<ContentTarget> targets;
    private int ptr = 0;
    boolean isCircular = false;

    private InMemoryTargetSupplier() {
    }

    public InMemoryTargetSupplier(final List<ContentTarget> targets) {
        this.targets = targets;
    }
    public InMemoryTargetSupplier(final List<ContentTarget> targets, boolean isCircular){
        this(targets);
        this.isCircular = isCircular;
    }

    @Override
    public synchronized ContentTarget get() {
        if (targets.isEmpty() || (!this.isCircular && (ptr >= targets.size()))) {
                return null;
        }
        ContentTarget res = targets.get(ptr);
        if (isCircular)
            ptr = ++ptr % targets.size();
        else
            ptr++;
        return res;
    }

}
