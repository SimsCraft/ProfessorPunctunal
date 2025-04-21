package com.simcraft.entities;
import java.awt.Image;

import javax.swing.JPanel;

public class Lecturer extends NPC {
    public Lecturer(JPanel panel, int xPos, int yPos, Image backgroundImage) {
        super(panel, xPos, yPos, 3, "Lecturers/FemLec", backgroundImage); // Medium speed
    }
}
