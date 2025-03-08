package ua.stetsenkoinna.LibNet;

import ua.stetsenkoinna.PetriObj.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import ua.stetsenkoinna.annotation.NetLibraryMethod;

public class NetLibrary {
    @NetLibraryMethod
    public static PetriNet workbenches() throws ExceptionInvalidTimeDelay {
        return createWorkbenches(new CreateNetArgs());
    }

    @NetLibraryMethod
    public static PetriNet verifyGeneratorShort() throws ExceptionInvalidTimeDelay {
        CreateNetArgs args = new CreateNetArgs();
        args.generatorMeanDelay = 10;
        return createWorkbenches(args);
    }

    @NetLibraryMethod
    public static PetriNet verifyGeneratorLong() throws ExceptionInvalidTimeDelay {
        CreateNetArgs args = new CreateNetArgs();
        args.generatorMeanDelay = 50;
        return createWorkbenches(args);
    }

    @NetLibraryMethod
    public static PetriNet verifyWorkbenchExpLong() throws ExceptionInvalidTimeDelay {
        CreateNetArgs args = new CreateNetArgs();
        args.workbenchExpMeanDelay = 35;
        return createWorkbenches(args);
    }

    @NetLibraryMethod
    public static PetriNet verifyWorkbenchExpShort() throws ExceptionInvalidTimeDelay {
        CreateNetArgs args = new CreateNetArgs();
        args.workbenchExpMeanDelay = 10;
        return createWorkbenches(args);
    }

    @NetLibraryMethod
    public static PetriNet verifyWorkbenchCount4() throws ExceptionInvalidTimeDelay {
        CreateNetArgs args = new CreateNetArgs();
        args.workbenchesCount = 4;
        return createWorkbenches(args);
    }

    public static PetriNet createWorkbenches(CreateNetArgs args) throws ExceptionInvalidTimeDelay {
        final ArrayList<PetriP> places = new ArrayList<>();
        final ArrayList<PetriT> transitions = new ArrayList<>();
        final ArrayList<ArcIn> inArcs = new ArrayList<>();
        final ArrayList<ArcOut> outArcs = new ArrayList<>();

        Settings.setMeanProcessingTime(args.workbenchExpMeanDelay);
        Settings.setMeanNormProcessingTime(args.workbenchNormMeanDelay);
        Settings.setDeviationNormProcessing(args.workbenchNormDeviation);
        Settings.setMeanOperationsCount(args.meanOperationsCount);
        Settings.setDeviationOperationsCount(args.deviationOperationsCount);
        Settings.setMaxOperationsCount(args.workbenchesCount);
        Settings.setMinOperationsCount(args.minOperationsCount);
        Settings.setAverageJobDelay(args.averageJobDelay);

        PetriP initial = new PetriP("");
        initial.setMark(1, true, false, false);
        places.add(initial);

        PetriT generator = new PetriT("Надходження робіт", args.generatorMeanDelay);
        generator.setTransitionType(TransitionType.JOB_GENERATOR);
        generator.setDistribution("exp", generator.getTimeServ());
        transitions.add(generator);

        PetriP queue = new PetriP("Черга робіт");
        queue.setMark(0, false, false, true);
        places.add(queue);

        inArcs.add(new ArcIn(initial, generator, 1));
        outArcs.add(new ArcOut(generator, initial, 1));
        outArcs.add(new ArcOut(generator, queue, 1));

        PetriP processed = new PetriP("Оброблені роботи");
        processed.setMark(0, true, false, true);
        places.add(processed);

        PetriP finalPlace = new PetriP("К-сть виконаних робіт");
        finalPlace.setMark(0, false, true, false);
        places.add(finalPlace);

        PetriT finished = new PetriT("Робота закінчена", 0);
        finished.setTransitionType(TransitionType.JOB_FINISHED_CHECKER);
        transitions.add(finished);

        PetriT unfinished = new PetriT("Робота не закінчена", 0);
        unfinished.setTransitionType(TransitionType.JOB_UNFINISHED_CHECKER);
        transitions.add(unfinished);

        inArcs.add(new ArcIn(processed, finished, 1));
        inArcs.add(new ArcIn(processed, unfinished, 1));
        outArcs.add(new ArcOut(finished, finalPlace, 1));
        outArcs.add(new ArcOut(unfinished, queue, 1));

        for (int i = 0; i < args.workbenchesCount; i++) {
            PetriT workbench = new PetriT("Обробка верст " + (i + 1), 0);
            workbench.setTransitionType(TransitionType.WORKBENCH);
            transitions.add(workbench);

            PetriP free = new PetriP("Верст " + (i + 1) + " вільний", 1);
            free.setMark(1, false, false, false);
            places.add(free);

            inArcs.add(new ArcIn(queue, workbench, 1));
            inArcs.add(new ArcIn(free, workbench, 1));
            outArcs.add(new ArcOut(workbench, free, 1));
            outArcs.add(new ArcOut(workbench, processed, 1));
        }

        PetriNet net = new PetriNet("Workbenches", places, transitions, inArcs, outArcs);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
        return net;
    }
}
