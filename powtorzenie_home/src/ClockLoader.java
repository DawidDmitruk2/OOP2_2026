import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClockLoader {

    public static List<CountryData> loadCases(String path) throws FileNotFoundException{
        List<CountryData> list = new ArrayList<>();
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        String firstLine = scanner.nextLine();
        String[] headerParts = firstLine.split(";");
        scanner.nextLine();

        String lastLine = "";
        while(scanner.hasNextLine()) {
            lastLine = scanner.nextLine();
        }
        String[] parts = lastLine.split(";");
        for (int i = 1; i < parts.length ;i++) {
            String country = headerParts[i];
            String cases = parts[i];

            try {
                int przypadki = Integer.parseInt(cases.trim());
                list.add(new CountryData(country, przypadki));
            } catch (NumberFormatException e) {
                System.out.println("error");
            }
        }

        Map<String, Integer> sumaPoKrajach = list.stream()
                .collect(Collectors.groupingBy(
                        CountryData::getName,                      // 1. Grupuj po nazwie
                        Collectors.summingInt(CountryData::getTotalCases) // 2. Sumuj przypadki w grupach
                ));

        List<String> wyniki = list.stream()
                .filter(c -> c.getTotalCases() > 1000000)
                .map(c -> c.getName())
                .toList();

        return list;
    }












//    public static List<Clock> loadFromCSV(String fileName) throws FileNotFoundException {
//        List<Clock> list = new ArrayList<>();
//        File file = new File(fileName);
//        Scanner scanner = new Scanner(file);
//
//        scanner.nextLine();

//        while(scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            String[] parts = line.split(",");
//            if(line.trim().isEmpty()) continue;
//
//            String[] coords = parts[2].trim().split(" ");
//            double north = Double.parseDouble(coords[0].trim());
//            int h = Integer.parseInt(parts[1].trim());
//            int m = Integer.parseInt(parts[2].trim());
//            int s = Integer.parseInt(parts[3].trim());
//
//            list.add(new DigitalClock(h,m,s));
//        }

//        while(scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            if(line.trim().isEmpty()) continue;
//
//            String[] parts = line.split(",");
//
//            int strefa = Integer.parseInt(parts[1].trim());
//            list.add(new DigitalClock(strefa, 0 ,0));
//        }

//        return list;
//    }
}