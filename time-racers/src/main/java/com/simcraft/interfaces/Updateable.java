package com.simcraft.interfaces;

/* An interface representing objects that require their logic to be updated every frame.
 * Any class implementing this interface must define a method to update 
 * itself.
 */
public interface Updateable {

    /**
     * Updates the object based on its internal logic.
     */
    void update();
}
