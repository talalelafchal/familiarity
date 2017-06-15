void foo(List<Person> persons) {
    persons.stream()
           .filter(it -> it.getAge() >= 21)
           .filter(it -> it.getName().startsWith("P"))
           .map(Person::getName)
           .sorted()
           .forEach(System.out::println);
}

class Person {
    final private String name;
    final private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    String getName() { return name; }
    int getAge() { return age; }
}