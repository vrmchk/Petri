package ua.stetsenkoinna.PetriObj;

public class Settings {
    private static double meanProcessingTime = 20;
    private static double meanNormProcessingTime = 0;
    private static double deviationNormProcessing = 0.3;
    private static double meanOperationsCount = 4;
    private static double deviationOperationsCount = 1;
    private static int minOperationsCount = 3;
    private static int maxOperationsCount = 6;

    public static double getMeanProcessingTime() {
        return meanProcessingTime;
    }

    public static void setMeanProcessingTime(double value){
        meanProcessingTime = value;
    }

    public static double getMeanNormProcessingTime() {
        return meanNormProcessingTime;
    }

    public static void setMeanNormProcessingTime(double meanNormProcessingTime) {
        Settings.meanNormProcessingTime = meanNormProcessingTime;
    }

    public static double getDeviationNormProcessing() {
        return deviationNormProcessing;
    }

    public static void setDeviationNormProcessing(double meanNormDeviation) {
        Settings.deviationNormProcessing = meanNormDeviation;
    }

    public static double getMeanOperationsCount() {
        return meanOperationsCount;
    }

    public static void setMeanOperationsCount(double meanOperationsCount) {
        Settings.meanOperationsCount = meanOperationsCount;
    }

    public static double getDeviationOperationsCount() {
        return deviationOperationsCount;
    }

    public static void setDeviationOperationsCount(double deviationOperationsCount) {
        Settings.deviationOperationsCount = deviationOperationsCount;
    }

    public static int getMinOperationsCount() {
        return minOperationsCount;
    }

    public static void setMinOperationsCount(int minOperationsCount) {
        Settings.minOperationsCount = minOperationsCount;
    }

    public static int getMaxOperationsCount() {
        return maxOperationsCount;
    }

    public static void setMaxOperationsCount(int maxOperationsCount) {
        Settings.maxOperationsCount = maxOperationsCount;
    }
}
