public class Main {
    public static void main(String[] args) {
        DeathCauseStatistic test = DeathCauseStatistic.fromCsvLine("A04.7          ,758,-,-,-,-,-,1,-,1,3,5,9,12,30,58,64,94,161,192,95,33");
        System.out.println(test);
        System.out.println(test.Age(93));

        DeathCauseStatisticsList statisticsList = new DeathCauseStatisticsList();
        statisticsList.repopulate("zgony.csv");
        System.out.println(statisticsList.mostDeadlyDiseases(2,4));
        ICDCodeTabularOptimizedForMemory tabular = new ICDCodeTabularOptimizedForMemory();
//        System.out.println(tabular.getDescription());


        test1 test1 = new test1(1, "nazwa1", 2);
        test2 test2 = new test2(3,"zawa", 5, 10);
        System.out.println(" ");
        System.out.println(test1.name + test1.num2);
        System.out.println(test2.name + test2.num3);
    }
}