import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DeathCauseStatisticsList {
    List<DeathCauseStatistic> deathCauseStatistics = new ArrayList<>();

    public void repopulate(String filePaths) {

        try {
            Scanner scanner = new Scanner(new File(filePaths));
            scanner.nextLine();
            scanner.nextLine();
            deathCauseStatistics.clear();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                DeathCauseStatistic parsed = DeathCauseStatistic.fromCsvLine(line);
                deathCauseStatistics.add(parsed);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file founded");
            throw new RuntimeException(e);
        }

    }

    public List<DeathCauseStatistic> mostDeadlyDiseases(int age, int n){
        if(n >= deathCauseStatistics.size()){
            throw new RuntimeException("Za duza liczba");
        }
        else{
            return deathCauseStatistics.stream()
                    .sorted((s1, s2) -> {
                        DeathCauseStatistic.AgeBracketDeaths a1 = s1.Age(age);
                        DeathCauseStatistic.AgeBracketDeaths a2 = s2.Age(age);
                        return a2.deathCount - a1.deathCount;
                    }).limit(n).toList();
        }
    }

    @Override
    public String toString() {
        return "deathCauseStatistics=" + deathCauseStatistics;
    }
}

