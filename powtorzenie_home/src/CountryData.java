public class CountryData {
    private String name;
    private int totalCases;

    // Konstruktor
    public CountryData(String name, int totalCases) {
        this.name = name;
        this.totalCases = totalCases;
    }

    // Gettery (przydadzą się później do Streamów!)
    public String getName() {
        return name;
    }
    public int getTotalCases() {
        return totalCases;
    }
}