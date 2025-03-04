package ua.stetsenkoinna.PetriObj;

public enum TransitionType {
    DEFAULT,
    WORKBENCH,
    JOB_FINISHED_CHECKER,
    JOB_UNFINISHED_CHECKER,
    JOB_GENERATOR;

    @Override
    public String toString() {
        // Convert enum name to a more readable format
        String formattedName = name().toLowerCase().replace("_", " ");
        return Character.toUpperCase(formattedName.charAt(0)) + formattedName.substring(1);
    }
}
