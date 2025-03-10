package ua.stetsenkoinna.LibTest;

import ua.stetsenkoinna.LibNet.NetLibrary;
import ua.stetsenkoinna.PetriObj.*;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Experiment {
    public static void main(String[] args) throws PythonExecutionException, IOException, ExceptionInvalidTimeDelay {
        plot();
        //stats();
        //experimentMeanValues();
        //basicValues();
        //test();
    }

    private static void plot() throws PythonExecutionException, IOException, ExceptionInvalidTimeDelay {
        ArrayList<RunResult> results = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            results.add(runNet(new CreateNetArgs(), 15000, 100));
        }
        Plot plt = Plot.create();

        for (RunResult result : results) {
            plt.plot().add(result.times, result.values);
        }

        plt.xlabel("Time modeling");
        plt.ylabel("Mean job delay");

        plt.show();
    }

    private static void stats() throws ExceptionInvalidTimeDelay {
        RunResult result = runNet(new CreateNetArgs(), 15000, 100);
        int transitionPeriod = 10000;
        validateStats(result, transitionPeriod);

        double sumValuesAndTimes = 0.0;
        double sumTimes = 0.0;
        for (int i = 0; i < result.times.size(); i++) {
            sumValuesAndTimes += result.values.get(i) * result.times.get(i);
            sumTimes += result.times.get(i);
        }
        double mean = sumValuesAndTimes / sumTimes;
        double sumMeanAndValuesAndTimes = 0.0;
        for (int i = 0; i < result.times.size(); i++) {
            sumMeanAndValuesAndTimes += (result.values.get(i) - mean) * (result.values.get(i) - mean) * result.times.get(i);
            sumTimes += result.times.get(i);
        }
        double sigma2 = sumMeanAndValuesAndTimes / sumTimes;
        System.out.println("Mean = " + mean);
        System.out.println("sigma2 = " + sigma2);
    }

    private static void experimentMeanValues() throws ExceptionInvalidTimeDelay {
        double modelingTime = 15000;
        ArrayList<Double> averageDelaysA1B1 = new ArrayList<>();
        ArrayList<Double> averageDelaysA1B2 = new ArrayList<>();
        ArrayList<Double> averageDelaysA2B1 = new ArrayList<>();
        ArrayList<Double> averageDelaysA2B2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            averageDelaysA1B1.add(getNetAverageDelay(NetLibrary.experimentA1B1(), modelingTime));
            averageDelaysA1B2.add(getNetAverageDelay(NetLibrary.experimentA1B2(), modelingTime));
            averageDelaysA2B1.add(getNetAverageDelay(NetLibrary.experimentA2B1(), modelingTime));
            averageDelaysA2B2.add(getNetAverageDelay(NetLibrary.experimentA2B2(), modelingTime));
        }

        System.out.println(MessageFormat.format("Mean A1B1 = {0}", averageDelaysA1B1.stream().mapToDouble(d -> d).average().orElse(0)));
        System.out.println(MessageFormat.format("Mean A1B2 = {0}", averageDelaysA1B2.stream().mapToDouble(d -> d).average().orElse(0)));
        System.out.println(MessageFormat.format("Mean A2B1 = {0}", averageDelaysA2B1.stream().mapToDouble(d -> d).average().orElse(0)));
        System.out.println(MessageFormat.format("Mean A2B2 = {0}", averageDelaysA2B2.stream().mapToDouble(d -> d).average().orElse(0)));
    }

    private static void test() throws ExceptionInvalidTimeDelay {
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                testValues(i * 5, 6 + j, results);
            }
        }

        for (String result : results) {
            System.out.println(result);
        }
    }

    private static void testValues(double averageDelay, int workbenchesCount, List<String> stringResults) throws ExceptionInvalidTimeDelay {
        ArrayList<RunResult> results = new ArrayList<>();
        double modelingTime = 15000;
        int transitionPeriod = 10000;
        CreateNetArgs args = new CreateNetArgs();
        args.averageJobDelay = averageDelay;
        args.workbenchesCount = workbenchesCount;
        for (int i = 0; i < 5; i++) {
            results.add(runNet(args, modelingTime, 100));
        }
        validateStats(results, transitionPeriod);

        stringResults.add(MessageFormat.format("Average delay: {0}, Workbenches: {1}, Mean job delay = {2}", averageDelay, workbenchesCount, results.stream().mapToDouble(r -> r.values.stream().mapToDouble(x -> x).average().orElse(0)).average().orElse(0)));
    }

    private static void basicValues() throws ExceptionInvalidTimeDelay {
        ArrayList<RunResult> results = new ArrayList<>();
        double modelingTime = 15000;
        int transitionPeriod = 10000;
        for (int i = 0; i < 5; i++) {
            results.add(runNet(new CreateNetArgs(), modelingTime, 100));
        }
        validateStats(results, transitionPeriod);

        System.out.println(MessageFormat.format("Average delay: 0, Workbenches: 6, Mean job delay = {0}", results.stream().mapToDouble(r -> r.values.stream().mapToDouble(x -> x).average().orElse(0)).average().orElse(0)));
    }

    private static RunResult runNet(CreateNetArgs args, double modelingTime, double statsInterval) throws ExceptionInvalidTimeDelay {
        PetriNet net = NetLibrary.createWorkbenches(args);
        PetriSim sim = new PetriSim(net);
        PetriObjModel petriObjModel = new PetriObjModel(new ArrayList<>(Collections.singletonList(sim)));
        petriObjModel.setIsProtokol(false);

        RunResult result = new RunResult();
        petriObjModel.go(modelingTime, time -> {
            result.times.add(time);
            PetriP finalPlace = Arrays.stream(petriObjModel.getObj().getNet().getListP()).filter(PetriP::isFinal).findFirst().orElse(null);
            double value = finalPlace.getAverageDelay();
            result.values.add(value);
        }, statsInterval);

        return result;
    }

    private static double getNetAverageDelay(PetriNet net, double modelingTime){
        PetriSim sim = new PetriSim(net);
        PetriObjModel petriObjModel = new PetriObjModel(new ArrayList<>(Collections.singletonList(sim)));
        petriObjModel.setIsProtokol(false);

        petriObjModel.go(modelingTime, i -> {}, -1);

        PetriP finalPlace = Arrays.stream(petriObjModel.getObj().getNet().getListP()).filter(PetriP::isFinal).findFirst().orElse(null);
        return finalPlace.getAverageDelay();
    }


    private static void validateStats(List<RunResult> results, double validateValue) {
        for (RunResult result : results) {
            validateStats(result, validateValue);
        }
    }

    private static void validateStats(RunResult result, double validateValue){
        long countToRemove = result.times.stream().filter(t -> t < validateValue/10).count();
        for (int i = 0; i < countToRemove; i++) {
            result.times.remove(0);
            result.values.remove(0);
        }
    }
}

class RunResult{
    public final List<Double> times = new ArrayList<>();
    public final List<Double> values = new ArrayList<>();
}