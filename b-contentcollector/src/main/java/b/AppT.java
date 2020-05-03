package b;

import com.mybaas.utils.ConsoleUtils;
import com.mybaas.utils.ResourceUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class AppT {

    static Function<Integer, Integer> multiplyCurry(int x) {
        return (Integer y) -> x * y;
    }

    public static void main(String[] args) throws IOException {


        List<String> stringList = new ArrayList<String>();

        stringList.add("One flew over the cuckoo's nest");
        stringList.add("To kill a muckingbird");
        stringList.add("Gone with the wind");

        Stream<String> stream = stringList.stream();

        stream.flatMap((value) -> {
            String[] split = value.split(" ");
            return (Stream<String>) Arrays.asList(split).stream();
        })
                .forEach((value) -> System.out.println(value))
        ;


        Stream.of(1, 3, 5, 7)
                .map(integer -> integer*2)
                .forEach(System.out::println);


        ConsoleUtils.clearScreen();
        ConsoleUtils.resetColour();
        String logo = ResourceUtils.getResourceAsString("logo.txt");
        ConsoleUtils.setColor(ConsoleUtils.AnsiColours.RED);
        ConsoleUtils.writeLine(logo);
        LocalDateTime dateTime = LocalDateTime.now();
        String dateString = dateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-DD-HH-mm-ss-SSS"));
        System.out.println(dateString);

        List<Integer> list = new ArrayList<Integer>();
        List<Integer> immutableList = Collections.unmodifiableList(list);

        list.add(1);
        list.add(2);
        System.out.printf("size of list %d,  unmodifiable size %d \n", list.size(), immutableList.size());



        System.out.println("-------------------------------------");
        List<String> list1 = new ArrayList<>();
        list1.add("Hebe");
        System.out.printf("List1 ref %s \n", Integer.toHexString(System.identityHashCode(list1)));
        System.out.printf("List1 value %s \n", list1.get(0));
        test(list1);
        System.out.println("     OUT METHOD");
        System.out.printf("List1 ref %s \n", Integer.toHexString(System.identityHashCode(list1)));
        System.out.printf("List1 value %s \n", list1.get(0));
    }

    public static void  test(final List<String> list) {
        System.out.println("     IN METHOD");

        System.out.printf("List1 value %s \n", list.get(0));
        System.out.printf("List1 ref %s  in the method \n", Integer.toHexString(System.identityHashCode(list)));
        list.set(0, "inmethod");
        System.out.println();
        System.out.printf("List1 value  %s set \n", list.get(0));
        System.out.printf("List1 ref %s  in the method \n", Integer.toHexString(System.identityHashCode(list)));
        System.out.println();
       // list = new ArrayList<>();

        //list.add("new one");
        System.out.printf("List1 value %s \n", list.get(0));
        System.out.printf("List1 ref %s  in the method \n", Integer.toHexString(System.identityHashCode(list)));

    }


}
