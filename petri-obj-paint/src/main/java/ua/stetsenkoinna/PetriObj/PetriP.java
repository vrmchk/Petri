package ua.stetsenkoinna.PetriObj;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * This class for creating the place of Petri net.
 *
 *  @author Inna V. Stetsenko
 */
public class PetriP extends PetriMainElement implements Cloneable, Serializable {

    private int mark;
    private String name;
    private int number;
    private double mean;
    private static int next = 0;//додано 1.10.2012, лічильник об"єктів
    private int observedMax;
    private int observedMin;
    // whether mark is a parameter; added by Katya 08.12.2016
    private boolean markIsParam = false;
    // param name
    private String markParamName = null;
    
    private String id; // for json unique number

    private boolean hasJobMarkerParameters;
    private boolean isFinal;
    private boolean isJobQueue;
    private ArrayList<Marker> markers = new ArrayList<>();

    /**
     *
     * @param n name of place
     * @param m quantity of markers
     */
    public PetriP(String n, int m) {
        name = n;
        mark = m;
        mean = 0;
        number = next; //додано 1.10.2012
        next++;
        observedMax = m;
        observedMin = m;
        id=null;
    }
    
     /**
     *
     * @param n - the name of place
     */
    public PetriP(String n) { //changed by Inna 21.03.2018
        this(n, 0);
        
    }
 
     /**
     *
     * @param id unique number for saving in server
     * @param n name of place
     * @param m quantity of markers
     */
    public PetriP(String id, String n, int m) { //added by Inna 21.03.2018
        this(n,m);
        this.id = id;
    }

    /**
     *
     * @param id unique number for saving in server
     * @param n - the name of place
     */
    public PetriP(String id, String n) { //added by Inna 21.03.2018
        this(id, n, 0);
        
    }
    
    /**
     * Create a place with parametrized number of markers
     */
    /*public PetriP(String placeName, String marksParameterName) {
        name = placeName;
        
        mean = 0;
        number = next; //додано 1.10.2012
        next++;
        
        id=null;
        this.setMarkParam(marksParameterName);
    }*/

    public PetriP(PetriP position) {
        this(position.getName(), position.getMark());
        number = next;
        next++;
    }
 
    
    public boolean markIsParam() {
        return markIsParam;
    }
    
    public String getMarkParamName() {
        return markParamName;
    }
    
    public void setMarkParam(String paramName) {
        if (paramName == null) {
            markIsParam = false;
            markParamName = null;
        } else {
            markIsParam = true;
            markParamName = paramName;
            mark = 0;
        }
    }
    /**
     * Set the counter of places to zero.
     */
    public static void initNext(){ //ініціалізація лічильника нульовим значенням
    
        next = 0;
    }

    /**
     * /**
     * Recalculates the mean value
     *
     * @param a value for recalculate of mean value (value equals product of
     * marking and time divided by time modeling)
     */
    public void changeMean(double a) {
        mean = mean + (mark - mean) * a;
    }

    /**
     *
     * @return mean value of quantity of markers
     */
    public double getMean() {
        return mean;
    }

    /**
     *
     * @param a value on which increase the quantity of markers
     */
    public void increaseMark(int a, ArrayList<Marker> markers) {
        mark += a;
        this.markers.addAll(markers);
        if (observedMax < mark) {
            observedMax = mark;
        }
        if (observedMin > mark) {
            observedMin = mark;
        }

//        if (isWorkbenchState){
//            markers.stream().map(m -> m.)
//            logAction.accept("\n\n");
//        }
    }

    /**
     *
     * @param a value on which decrease the quantity of markers
     */
    public ArrayList<Marker> decreaseMark(int a, UUID transitionUuid, double currentTime, Consumer<String> logAction) {
        mark -= a;
        if (observedMax < mark) {
            observedMax = mark;
        }
        if (observedMin > mark) {
            observedMin = mark;
        }

        if (transitionUuid != null && isJobQueue) {
            sortMarkers(currentTime, logAction);
            JobMarker markerToPop = markers.stream().map(m -> (JobMarker)m).filter(m -> !m.getUsedTransitionUuids().contains(transitionUuid)).findFirst().orElse(null);
            markers.remove(markerToPop);
            logAction.accept(MessageFormat.format("\nJob {0} sent to workbench {1}, used transitions: [{2}]\n\n",
                    markerToPop.getUuid(), transitionUuid, markerToPop.getUsedTransitionUuids().stream().map(UUID::toString).collect(Collectors.joining(", "))));
            return new ArrayList<>(Arrays.asList(markerToPop));
        }

        return Utils.popFirst(markers, a);
    }

    /**
     *
     * @return current quantity of markers
     */
    public int getMark() {
        return mark;
    }
 /**
     * Set quantity of markers
     *
     * @param a quantity of markers
     */
    public void setMark(int a, boolean hasJobMarkerParameters, boolean isFinal, boolean isJobQueue) {
        mark = a;
        this.hasJobMarkerParameters = hasJobMarkerParameters;
        this.isFinal = isFinal;
        this.isJobQueue = isJobQueue;
        if (observedMax < mark) {
            observedMax = mark;
        }
        if (observedMin > mark) {
            observedMin = mark;
        }
        if (hasJobMarkerParameters || isJobQueue)
            markers = new ArrayList<>(Stream.generate(JobMarker::new).limit(a).collect(Collectors.toList()));
        else
            markers = new ArrayList<>(Stream.generate(Marker::new).limit(a).collect(Collectors.toList()));
    }

    
    public int getObservedMax() {
        return observedMax;
    }

    public int getObservedMin() {
        return observedMin;
    }

    /**
     *
     * @return name of the place
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param s - the new name of place
     */
    public void setName(String s) {
        name = s;
    }

    /**
     *
     * @return number of the place
     */
    public int getNumber() {
        return number;
    }

    /**
     *
     * @param n - the new number of place
     */
    public void setNumber(int n) {
        number = n;
    }

    
    /**
     *
     * @return PetriP object with parameters which copy current parameters of
     * this place
     * @throws java.lang.CloneNotSupportedException if Petri net has invalid structure
     */
    @Override
    public PetriP clone() throws CloneNotSupportedException {
        super.clone();
        PetriP P = new PetriP(name, this.getMark()); // 14.11.2012
        P.setNumber(number); //номер зберігається для відтворення зв"язків між копіями позицій та переходів
        return P;
    }

    public void printParameters() {
        System.out.println("Place " + name + "has such parametrs: \n"
                + " number " + number + ", mark " + mark);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public ArrayList<Marker> getMarkersSlice(int count) {
        return new ArrayList<>(markers.subList(0, count));
    }

    public boolean hasJobMarkerParameters() {
        return hasJobMarkerParameters;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public double getAverageJobCompletionTime(){
        return isFinal
                ? markers.stream()
                .filter(m -> m instanceof JobMarker)
                .map(m -> (JobMarker) m)
                .mapToDouble(JobMarker::getActualCompletionTime)
                .average()
                .orElse(0)
                : 0;
    }

    public double getAverageDelayBetweenCreations(){
        if (!isFinal)
            return 0;

        List<Double> creationTimes = markers.stream()
                .filter(m -> m instanceof JobMarker)
                .map(m -> ((JobMarker) m).getCreationTime())
                .sorted() // Sort times in ascending order
                .collect(Collectors.toList());

        if (creationTimes.size() < 2) return 0.0;

        return IntStream.range(0, creationTimes.size() - 1)
                .mapToDouble(i -> creationTimes.get(i + 1) - creationTimes.get(i))
                .average()
                .orElse(0.0);
    }

    public int getCountOfLateJobs(){
        return isFinal
                ? (int) markers.stream()
                    .filter(m -> m instanceof JobMarker)
                    .map(m -> (JobMarker) m)
                    .filter(m -> m.getExpectedFinishTime() < m.getActualFinishTime())
                    .count()
                : 0;
    }

    public double getAverageDelay(){
        return isFinal
                ? markers.stream()
                    .filter(m -> m instanceof JobMarker)
                    .map(m -> (JobMarker) m)
                    //.filter(m -> m.getActualFinishTime() > m.getExpectedFinishTime())
                    .mapToDouble(m -> m.getActualFinishTime() - m.getExpectedFinishTime())
                    .average()
                    .orElse(0)
                : 0;
    }

    public boolean isJobQueue() {
        return isJobQueue;
    }

    public void sortMarkers(double currentTime, Consumer<String> logAction){
        if (!isJobQueue || markers.size() == 0)
            return;

        if (markers.size() == 1) {
            logAction.accept(MessageFormat.format("\nOnly one marker in queue, no need to sort, priority: [{0}]\n", ((JobMarker)markers.get(0)).getPriority(currentTime)));
            return;
        }

        logAction.accept(MessageFormat.format("\nMarkets priorities before sorting [{0}]\n",
                markers.stream().map(m -> Double.toString(((JobMarker)m).getPriority(currentTime))).collect(Collectors.joining(", "))));

        markers.sort(Comparator.comparingDouble(m -> ((JobMarker)m).getPriority(currentTime)));

        logAction.accept(MessageFormat.format("\nMarkets priorities after sorting [{0}]\n",
                markers.stream().map(m -> Double.toString(((JobMarker)m).getPriority(currentTime))).collect(Collectors.joining(", "))));
    }
}
