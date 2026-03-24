import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Person implements Comparable<Person> {
    private Set<Person> children;
    private String name;
    private String surname;
    private LocalDate date;

    public List<Person> getChildren(){
//        List<Person> resultList = new ArrayList<>();
//        for(Person p : children){
//            resultList.add(p);
//        }
//        resultList.sort(Person::compareTo);
//        return resultList;
        return children.stream().sorted().toList();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

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
            if (result == null || result.compareTo(p) < 0) {
                result = p;
            }
        }
        return result;
    }

    @Override
    public int compareTo(Person other) {
        if (this.date.getYear() == other.date.getYear()) {
            return this.date.getDayOfYear() - other.date.getDayOfYear();
        }
        return this.date.getYear() - other.date.getYear();
    }
}