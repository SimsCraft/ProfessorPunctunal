package com.simcraft.levels;

import java.util.List;

import com.simcraft.entities.Entity;
import com.simcraft.interfaces.Renderable;
import com.simcraft.interfaces.Updateable;

/**
 * Represents a playable level's core configuration and state.
 */
public abstract class AbstractLevel implements Renderable, Updateable {

    // ----- ATTRIBUTES -----
    private String name;
    private int width, height;
    private String backgroundImagePath;
    private List<Entity> entities; // Could include player, NPCs, obstacles, etc.
    private String musicTrack;

    // ----- CONSTRUCTOR -----
    

    // ----- GETTERS / SETTERS -----

    // ----- GAME LOGIC METHODS -----
    public boolean isComplete(); // Check if win condition is met
}
