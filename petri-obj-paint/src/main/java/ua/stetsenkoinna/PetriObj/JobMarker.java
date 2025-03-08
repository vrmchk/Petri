package ua.stetsenkoinna.PetriObj;

import java.util.ArrayList;
import java.util.UUID;

public class JobMarker extends Marker {
    private int operationsCount;
    private int currentOperationsCount;
    private double expectedCompletionTime;
    private double creationTime;
    private double expectedFinishTime;
    private double actualFinishTime;
    private final ArrayList<UUID> usedTransitionUuids = new ArrayList<>();
    private final double securityFactorMultiplierTime = 1;

    public double getActualFinishTime() {
        return actualFinishTime;
    }

    public ArrayList<UUID> getUsedTransitionUuids() {
        return usedTransitionUuids;
    }

    public int getCurrentOperationsCount() {
        return currentOperationsCount;
    }

    public void decreaseCurrentOperationsCount(double currentTime) {
        currentOperationsCount--;
        if (currentOperationsCount == 0)
            actualFinishTime = currentTime;
    }

    public double getCreationTime() {
        return creationTime;
    }

    public double getExpectedCompletionTime() {
        return expectedCompletionTime;
    }

    public double getOperationsCount() {
        return operationsCount;
    }

    public void setCreationTime(double creationTime) {
        this.creationTime = creationTime;
        this.expectedFinishTime = creationTime + expectedCompletionTime + Settings.getAverageJobDelay();
    }

    public double getExpectedFinishTime() {
        return expectedFinishTime;
    }

    public double getPriority(double currentTime){
        return expectedFinishTime - currentTime - currentOperationsCount * Settings.getMeanProcessingTime() - currentOperationsCount * securityFactorMultiplierTime;
    }

    public void setOperationsCount(int operationsCount) {
        this.operationsCount = operationsCount;
        currentOperationsCount = operationsCount;
        expectedCompletionTime = operationsCount * Settings.getMeanProcessingTime();
    }

    public double getActualCompletionTime(){
        return actualFinishTime - creationTime;
    }
}
