package ua.stetsenkoinna.PetriObj;

import java.io.Serializable;

public class Marker extends PetriMainElement implements Cloneable, Serializable {
    @Override
    public Marker clone() {
        try {
            return (Marker) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

