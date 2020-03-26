package b.contentcollector.targetsuppliers;


import b.abnd.InMemoryTargetSupplier;
import b.contentcollector.model.ContentTarget;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;


import static org.junit.Assert.*;

public class InMemoryTargetSupplierTest {
    List<ContentTarget> targets;
    @Before
    public void setUp() throws Exception {
        System.out.println("before -------------------------");
        targets = new ArrayList<>();
         /*   targets.add(new Target("925081d4-1cd2-45bc-9553-8312b9ac98bd", "http://www.google.com"));
            targets.add(new Target("a2b0e824-d2c8-47ae-a8b7-92ecb284225d", "http://www.yahoo.com"));
            targets.add(new Target("00d225a2-d5d9-459c-ac6e-4f5b2d9b8dc0", "http://www.bing.com"));
*/
    }
    @BeforeClass
    public  static void beforeClass() {
        System.out.println("before class-------------------------");
    }
    @Ignore
    @Test
    public void testInCircular() {
        System.out.println("test incircular -------------------------");
        Supplier<ContentTarget> supplier = new InMemoryTargetSupplier(targets);
        int i;
        for (i = 0; i < targets.size(); i++) {
            ContentTarget t = supplier.get();
        }

        ContentTarget t = supplier.get();
        boolean passed = Objects.isNull(t) && i==targets.size();
        assertTrue(String.format("class %s can not pass testing InCircular supply.", this.getClass().getName()), passed);
    }
    @Ignore
    @Test
    public void testCircular() {
        System.out.println("test circular -------------------------");
        Supplier<ContentTarget> supplier = new InMemoryTargetSupplier(targets, true);
        int i;
        ContentTarget t = supplier.get();
        for (i = 1; i < targets.size(); i++) {
            ContentTarget temp = supplier.get();
        }

        ContentTarget t2 = supplier.get();
        boolean passed = Objects.nonNull(t2) && t.equals(t2);
        assertTrue(String.format("class %s can not pass testing Circular supply.", this.getClass().getName()), passed);
    }
}