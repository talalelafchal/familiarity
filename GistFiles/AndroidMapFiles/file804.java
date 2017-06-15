import lombok.Data;
import com.annimon.stream.Stream;

void foo(List<Person> persons) {
    Stream.of(persons)
          .filter(it -> it.getAge() >= 21)
          .filter(it -> it.getName().startsWith("P"))
          .map(Person::getName)
          .sorted()
          .forEach(System.out::println);
}

@Data class Person {
    final String name;
    final int age;
}