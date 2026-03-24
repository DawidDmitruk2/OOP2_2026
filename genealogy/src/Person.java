import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Person {
    private Set<Person> children;
    private String name;
    private String surname;
    private LocalDate date;

    public Person(String name, String surname, LocalDate date){
        this.name = name;
        this.surname = surname;
        this.date = date;
        this.children = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", date=" + date +
                '}';
    }

    public boolean adopt(Person child) {
        boolean success = this.children.add(child);
        return success;
    }

    public Person getYoungestChild() {
        Person result = null;
        for(Person p : this.children) {
            if (result == null || result.date.isBefore(p.date)) {
                result = p;
            }
        }
        return result;
    }
}
