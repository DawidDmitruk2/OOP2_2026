import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ICDCodeTabularOptimizedForMemory implements ICDCodeTabular{
    @Override
    public String getDescription(String icd10) {
        try {
            Scanner scanner = new Scanner(new File("icd10.txt"));
            for (int i = 0; i < 87; i++)
                scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith(icd10)) {
                    String[] parts = line.split(" ", 2);
                    return parts[1];
                }
            }
            throw new IndexOutOfBoundsException("error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}