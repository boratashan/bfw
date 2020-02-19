package b.breadboard;

interface One {
    default void method() {
        System.out.println("");
    }
}
interface Two {
    default void method() {
        System.out.println("");
    }
}
public class Intftest implements One, Two {
    @Override
    public void method() {
        

    }
}
