import lombok.Data;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Streams.class)
public class Foo {
    void foo(List<Person> persons) {
        persons.toStream()
               .filter(it -> it.getAge() >= 21)
               .filter(it -> it.getName().startsWith("P"))
               .map(Person::getName)
               .sorted()
               .forEach(System.out::println);
    }
}

@Data class Person {
    final String name;
    final int age;
}

class Streams {
    static <T> Stream<T> toStream(List<T> list) {
        return Stream.of(list);
    }
}