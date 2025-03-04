package ua.stetsenkoinna.PetriObj;

import java.util.UUID;

/**
 *
 * @author Katya (added 20.11.2016)
 */
public class PetriMainElement {
    protected final UUID uuid;

    public PetriMainElement(){
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}
